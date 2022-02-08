package com.yandex.pay.core.state

import com.yandex.pay.core.data.OAuthToken
import com.yandex.pay.core.infra.State

internal data class AuthorizationState(
    val token: OAuthToken,
    val loading: Boolean,
) : State {
    internal companion object {
        fun create(): AuthorizationState = AuthorizationState(OAuthToken.empty(), false)
    }
}
