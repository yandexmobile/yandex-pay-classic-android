package com.yandex.pay.core.utils

internal fun interface Provider<T> {
    operator fun invoke(): T
}
