package com.yandex.pay.core.data

@JvmInline
internal value class Uid(internal val value: String) {
    val isEmpty: Boolean
        get() = this == empty()

    internal companion object {
        fun empty(): Uid = Uid("")
    }
}
