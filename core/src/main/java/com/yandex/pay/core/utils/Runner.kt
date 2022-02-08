package com.yandex.pay.core.utils

internal fun interface Runner {
    operator fun invoke(action: () -> Unit)
}
