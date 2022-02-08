package com.yandex.xplat.common

import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.RequestBody

class EncodedRequestParameters(
    val queryParameters: Map<String, Any?>,
    val body: RequestBody?
)

private val METHODS_ENCODED_IN_URL = setOf("GET", "HEAD", "DELETE")

private sealed class RequestEncoder(val jsonSerializer: JSONSerializer) {
    abstract fun encodeRequest(params: NetworkParams): EncodedRequestParameters

    class UrlRequestEncoder(private val method: NetworkMethod, jsonSerializer: JSONSerializer) : RequestEncoder(jsonSerializer) {
        override fun encodeRequest(params: NetworkParams): EncodedRequestParameters {
            val encodesParametersInUrl = METHODS_ENCODED_IN_URL.contains(method.method)
            return if (encodesParametersInUrl) {
                EncodedRequestParameters(JSONItemSupport.from(params) as Map<String, *>, null)
            } else {
                val body = buildFormBody(params)
                EncodedRequestParameters(
                        mapOf(),
                        body
                )
            }
        }

        private fun buildFormBody(params: NetworkParams): RequestBody {
            val builder = FormBody.Builder()
            (JSONItemSupport.from(params) as Map<String, *>).toSortedMap().forEach { (key, value) ->
                stringifyQueryParam(value)?.let { builder.add(key, it) }
            }
            return builder.build()
        }
    }

    class JsonRequestEncoder(jsonSerializer: JSONSerializer) : RequestEncoder(jsonSerializer) {
        override fun encodeRequest(params: NetworkParams): EncodedRequestParameters {
            val body = buildJsonBody(params)
            return EncodedRequestParameters(
                mapOf(),
                body
            )
        }

        private fun buildJsonBody(params: NetworkParams): RequestBody {
            val content = this.jsonSerializer.serialize(params)
            val result = if (content.isError()) {
                Log.error("Error building JSON POST request body: ${content.getError().message}")
                ""
            } else {
                content.getValue()
            }
            return RequestBody.create(MediaType.get("application/json"), result)
        }
    }
}

fun encodeRequest(
    jsonSerializer: JSONSerializer,
    encoding: RequestEncoding,
    method: NetworkMethod,
    params: NetworkParams
): EncodedRequestParameters =
        when (encoding.kind) {
            RequestEncodingKind.url -> RequestEncoder.UrlRequestEncoder(method, jsonSerializer).encodeRequest(params)
            RequestEncodingKind.json -> RequestEncoder.JsonRequestEncoder(jsonSerializer).encodeRequest(params)
        }
