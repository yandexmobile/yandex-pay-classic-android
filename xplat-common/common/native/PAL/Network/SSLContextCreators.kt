package com.yandex.xplat.common

import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import okhttp3.OkHttpClient

class TrustAllSSLContextCreator internal constructor() : SSLContextCreator {
    private val trustAllCerts: Array<TrustManager> = arrayOf(
        object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        }
    )

    override fun createSSLConfiguredClient(httpBuilder: OkHttpClient.Builder): OkHttpClient.Builder {
        val sslContext = SSLContext.getInstance("SSL")
        if (sslContext != null) {
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())
            httpBuilder
                .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
        }
        return httpBuilder
    }
}

class NoOpSSLContextCreator internal constructor() : SSLContextCreator {
    override fun createSSLConfiguredClient(httpBuilder: OkHttpClient.Builder): OkHttpClient.Builder {
        return httpBuilder
    }
}

fun createSSLContentCreator(trustAll: Boolean): SSLContextCreator {
    return if (trustAll) TrustAllSSLContextCreator() else NoOpSSLContextCreator()
}
