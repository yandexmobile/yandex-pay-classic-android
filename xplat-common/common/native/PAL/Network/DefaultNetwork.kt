package com.yandex.xplat.common

import com.squareup.moshi.Moshi
import java.io.IOException
import java.net.URL
import java.nio.charset.Charset
import kotlin.text.Charsets.UTF_8
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor

val NetworkMethod.method: String
    get() = when (this) {
        NetworkMethod.get -> "GET"
        NetworkMethod.post -> "POST"
    }

data class NetworkConfig(
    val isConsoleLoggingEnabled: Boolean,
    val sslContextCreator: SSLContextCreator?,
    val interceptors: List<Interceptor> = listOf(),
    val stethoProxy: StethoProxy? = null,
    val dns: Dns? = null
)

class DefaultNetworkResponseBody(
    private val charset: Charset,
    private val bytes: ArrayBuffer
) : NetworkResponseBody {
    override fun string() = bytes.byteArray.toString(charset)
    override fun bytes() = bytes
}

class DefaultNetworkResponse(
    private val code: Int,
    private val headers: YSMap<String, String>,
    private val isSuccessful: Boolean,
    private val body: NetworkResponseBody?
) : NetworkResponse {
    override fun code() = code
    override fun headers() = headers
    override fun isSuccessful() = isSuccessful
    override fun body() = body
}

class DefaultNetwork(
    private val baseUrlProvider: () -> URL,
    config: NetworkConfig?,
    private val jsonSerializer: JSONSerializer
) : Network {

    constructor(baseURL: URL, config: NetworkConfig?, jsonSerializer: JSONSerializer) :
        this({ baseURL }, config, jsonSerializer)

    private val httpClient: OkHttpClient = run {
        val builder = OkHttpClient.Builder()

        if (config != null) {
            if (config.isConsoleLoggingEnabled) {
                builder.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            }
            config.interceptors.forEach { builder.addInterceptor(it) }
            config.stethoProxy?.patch(builder)
            config.sslContextCreator?.createSSLConfiguredClient(builder)
            if (config.dns != null) {
                builder.dns(config.dns)
            }
        }

        val dispatcher = Dispatcher(BoundExecutor.operationsExecutor("NetworkRequestExecutor")).apply {
            maxRequests = 1
        }
        builder.dispatcher(dispatcher).build()
    }
    private val callbackExecutor = BoundExecutor.ResultsExecutor()
    private val moshi = Moshi.Builder().build()!!

    override fun execute(request: NetworkRequest): XPromise<JSONItem> =
        runRawRequest { createHttpRequest(request) }.flatThen { response -> toPromise(handleJsonResponse(response)) }

    override fun executeRaw(request: NetworkRequest): XPromise<NetworkResponse> {
        return runRawRequest { createHttpRequest(request) }
    }

    override fun resolveURL(request: NetworkRequest): String? =
        createHttpRequest(request).url().toString()

    private fun runRawRequest(requestProvider: () -> Request): XPromise<NetworkResponse> {
        val deferred = deferred<NetworkResponse>(callbackExecutor.executor)
        httpClient.newCall(requestProvider()).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) =
                deferred.reject(NetworkError.NetworkErrorTransportFailure("Error communicating with the server: $e", e))

            override fun onResponse(call: Call, response: Response) {
                val (charset, bodyBytes) = try {
                    Pair(
                        response.body()?.contentType()?.charset(UTF_8) ?: UTF_8,
                        response.body()?.bytes()
                    )
                } catch (e: IOException) {
                    return deferred.reject(NetworkError.NetworkErrorTransportFailure("Error obtaining response body string", e))
                }

                val headers = response.headers().toMultimap().mapValues { it.value.join(", ") }
                val rawResponse = DefaultNetworkResponse(
                    response.code(),
                    headers.toMutableMap(),
                    response.isSuccessful,
                    bodyBytes?.let { DefaultNetworkResponseBody(charset, ArrayBuffer(it)) }
                )
                deferred.resolve(rawResponse)
            }
        })
        return deferred.promise
    }

    private fun handleJsonResponse(rawResponse: NetworkResponse): Result<JSONItem> {
        return if (!rawResponse.isSuccessful()) {
            Result(null, NetworkError.NetworkErrorBadCode(rawResponse.code()))
        } else {
            rawResponse.body()?.string()?.let { jsonSerializer.deserialize(it) }
                ?: Result<JSONItem>(null, NetworkError.NetworkErrorNoData)
        }
    }

    private fun createHttpRequest(request: NetworkRequest): Request {
        val encodedParameters = encodeRequest(this.jsonSerializer, request.encoding(), request.method(), request.params())

        val urlBuilder = HttpUrl.get(baseUrlProvider.invoke())!!.newBuilder()
            .addPathSegments(request.targetPath())

        val queryParameters = JSONItemSupport.from(request.urlExtra()) as Map<String, *> + encodedParameters.queryParameters
        queryParameters.forEach { (key, value) ->
            stringifyQueryParam(value)?.let { urlBuilder.addQueryParameter(key, it) }
        }

        val builder = Request.Builder().url(urlBuilder.build())
            .addHeader("Connection", "keep-alive")
        encodedParameters.body?.let {
            builder.addHeader("Content-Type", it.contentType().toString())
        }

        val headers = JSONItemSupport.from(request.headersExtra()) as Map<String, *>
        headers.forEach { (key, value) ->
            stringifyQueryParam(value)?.let { builder.addHeader(key, it) }
        }

        builder.method(request.method().method, encodedParameters.body)
        return builder.build()
    }
}

fun stringifyQueryParam(value: Any?): String? =
    when (value) {
        is Number -> value.toString()
        is String -> value
        true -> "yes"
        false -> "no"
        null -> "null"
        else -> null
    }
