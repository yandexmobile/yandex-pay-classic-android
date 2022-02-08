package com.yandex.pay.core.middleware

import com.yandex.pay.core.YandexPayLib
import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.actions.AuthorizationAction
import com.yandex.pay.core.actions.NavigationAction
import com.yandex.pay.core.infra.Dispatch
import com.yandex.pay.core.infra.Middleware
import com.yandex.pay.core.infra.Next
import com.yandex.pay.core.navigation.Route
import com.yandex.pay.core.state.AppState
import com.yandex.pay.core.storage.AuthTokenStorage
import com.yandex.pay.core.storage.CurrentCardStorage
import com.yandex.pay.core.storage.UserProfileStorage
import com.yandex.pay.core.userprofile.AvatarLoader

internal class AuthorizationMiddleware(
    private val tokenStorage: AuthTokenStorage,
    private val userProfileStorage: UserProfileStorage,
    private val avatarLoader: AvatarLoader,
    private val currentCardStorage: CurrentCardStorage,
) : Middleware {
    override fun handle(state: AppState, action: Action, next: Next, dispatch: Dispatch): Action =
        when (action) {
            is AuthorizationAction.Authorize -> action.also {
                state.navigation.value!!.router?.push(Route.Authorization)
            }
            is AuthorizationAction.StoreToken -> next(state, action)
            is AuthorizationAction.Complete -> action.also {
                YandexPayLib.instance.componentsHolder.resetApi()
                state.navigation.value!!.router?.pull()
            }
            is AuthorizationAction.Cancel -> next(state, NavigationAction.Complete(null))
            is AuthorizationAction.InvalidateStoredToken -> action.also {
                dropLoginData()
                YandexPayLib.instance.componentsHolder.resetApi()
            }
            else -> next(state, action)
        }

    private fun dropLoginData() {
        tokenStorage.drop()
        currentCardStorage.drop()
        userProfileStorage.drop()
        avatarLoader.drop()
    }
}
