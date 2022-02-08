package com.yandex.pay.core.infra

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.state.AppState

internal fun interface Middleware {
    fun handle(state: AppState, action: Action, next: Next, dispatch: Dispatch): Action
}
