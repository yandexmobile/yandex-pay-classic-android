package com.yandex.pay.core

interface Result<T, E> {
    fun onSuccess(value: T)
    fun onFailure(error: E)
}
