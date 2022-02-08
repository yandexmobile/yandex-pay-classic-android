package com.yandex.pay.core.storage

internal interface Storage<T> {
    fun load(): T?
    fun save(value: T)
    fun drop()
}
