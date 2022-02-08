package com.yandex.pay.core.middleware

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.actions.NavigationAction
import com.yandex.pay.core.infra.Dispatch
import com.yandex.pay.core.infra.Middleware
import com.yandex.pay.core.infra.Next
import com.yandex.pay.core.state.AppState

internal class NavigationMiddleware : Middleware {
    override fun handle(state: AppState, action: Action, next: Next, dispatch: Dispatch): Action =
        when (action) {
            is NavigationAction.Push -> action.also {
                state.navigation.value!!.router?.push(it.route)
            }
            is NavigationAction.Pull -> action.also {
                state.navigation.value!!.router?.pull(action.toRoute)
            }
            is NavigationAction.Replace -> action.also {
                state.navigation.value!!.router?.replace(it.route)
            }
            is NavigationAction.Complete -> action.also {
                state.navigation.value!!.router?.finish(it.checkoutResult)
            }
            is NavigationAction.CompleteWithError -> action.also {
                state.navigation.value!!.router?.finishWithError(it.error)
            }
            else -> next(state, action)
        }
}
