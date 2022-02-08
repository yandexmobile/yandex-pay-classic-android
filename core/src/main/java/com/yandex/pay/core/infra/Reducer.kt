package com.yandex.pay.core.infra

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.state.AppState

internal fun interface Reducer {
    fun reduce(state: AppState, action: Action): AppState
}
