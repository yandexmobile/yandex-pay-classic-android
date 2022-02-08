package com.yandex.pay.core.state

import com.yandex.pay.core.data.ErrorType
import com.yandex.pay.core.infra.State

internal data class GeneralState(
    val error: ErrorType?,
) : State {
    internal companion object {
        fun create(): GeneralState = GeneralState(null)
    }
}
