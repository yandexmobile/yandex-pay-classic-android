package com.yandex.xplat.common

class YSSet<T>(val values: MutableSet<T> = mutableSetOf()) {
    constructor(iterable: Iterable<T>) : this(iterable.toMutableSet())
    constructor(other: YSSet<T>) : this(other.values.toMutableSet())

    val size: Int get() = values.size

    fun add(value: T) = values.apply { add(value) }

    fun has(value: T) = values.contains(value)

    fun values(): Iterable<T> = values

    fun delete(item: T): Boolean = values.remove(item)

    fun forEach(action: (T) -> Unit) {
        for (element in this.values()) action(element)
    }

    fun clear() = values.clear()
}
