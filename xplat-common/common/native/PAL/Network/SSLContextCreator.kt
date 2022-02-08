package com.yandex.xplat.common

import okhttp3.OkHttpClient

interface SSLContextCreator {
    fun createSSLConfiguredClient(httpBuilder: OkHttpClient.Builder): OkHttpClient.Builder
}
