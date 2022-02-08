package com.yandex.pay.core.data

import com.yandex.authsdk.YandexAuthToken

@JvmInline
internal value class OAuthToken(internal val value: String) {
    val isEmpty: Boolean
        get() = this == empty()

    internal companion object {
        fun from(authToken: YandexAuthToken): OAuthToken = OAuthToken(authToken.value)
        fun empty(): OAuthToken = OAuthToken("")
    }
}
