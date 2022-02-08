package com.yandex.xplat.common

fun <T> resultify(block: () -> T): Result<T> = try {
    Result(block(), null)
} catch (e: YSError) {
    Result(null, e)
}
