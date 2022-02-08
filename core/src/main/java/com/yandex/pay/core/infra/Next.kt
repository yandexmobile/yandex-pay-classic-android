package com.yandex.pay.core.infra

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.state.AppState

internal fun interface Next {
    operator fun invoke(state: AppState, action: Action): Action
}
