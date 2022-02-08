package com.yandex.pay.core.actions

import com.yandex.pay.core.data.OAuthToken

internal sealed interface AuthorizationAction : Action {
    object Authorize : AuthorizationAction
    object InvalidateStoredToken : AuthorizationAction
    class StoreToken(val token: OAuthToken) : AuthorizationAction
    object Complete : AuthorizationAction
    object Cancel : AuthorizationAction
}
