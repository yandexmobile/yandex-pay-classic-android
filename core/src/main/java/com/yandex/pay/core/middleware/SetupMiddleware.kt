package com.yandex.pay.core.middleware

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.actions.GeneralActions
import com.yandex.pay.core.actions.NavigationAction
import com.yandex.pay.core.infra.Dispatch
import com.yandex.pay.core.infra.Middleware
import com.yandex.pay.core.infra.Next
import com.yandex.pay.core.navigation.Route
import com.yandex.pay.core.state.AppState

internal class SetupMiddleware : Middleware {
    override fun handle(state: AppState, action: Action, next: Next, dispatch: Dispatch): Action =
        when (action) {
            is GeneralActions.KickOff -> action.also {
                next(state, NavigationAction.Push(Route.GetCards))
            }
            else -> next(state, action)
        }
}
