package com.yandex.pay.core.infra

import com.yandex.pay.core.actions.Action

internal fun interface Dispatch {
    operator fun invoke(action: Action)
}
