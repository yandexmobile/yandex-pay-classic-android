package com.yandex.xplat.common

import okhttp3.OkHttpClient

/**
 * This interface is required for hiding Stetho from release builds.
 */
interface StethoProxy {
    fun init()

    fun patch(clientBuilder: OkHttpClient.Builder)
}
