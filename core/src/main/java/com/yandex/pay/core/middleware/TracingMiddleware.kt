package com.yandex.pay.core.middleware

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.infra.Dispatch
import com.yandex.pay.core.infra.Middleware
import com.yandex.pay.core.infra.Next
import com.yandex.pay.core.state.AppState

internal class TracingMiddleware(
    private val enabled: Boolean,
    private val logger: (String) -> Unit
) : Middleware {
    override fun handle(state: AppState, action: Action, next: Next, dispatch: Dispatch): Action {
        if (enabled) {
            logger(action.toString())
        }
        return next(state, action)
    }
}
