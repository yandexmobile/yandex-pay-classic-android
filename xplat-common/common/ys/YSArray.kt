package com.yandex.xplat.common

import kotlin.math.max
import kotlin.math.min

typealias YSArray<E> = MutableList<E>

inline fun <T, R> YSArray<out T>.map(transform: (T) -> R): YSArray<R> = mapTo(mutableListOf(), transform)

fun <T> YSArray<T>.concat(c: Collection<T>): YSArray<T> = toMutableList().also { it.addAll(c) }

fun <T> YSArray<T>.filter(predicate: (T, Int) -> Boolean): YSArray<T> = filterIndexed { index, element -> predicate(element, index) }.toMutableList()

fun <T> Iterable<T>.filter(predicate: (T) -> Boolean): YSArray<T> = filterTo(mutableListOf(), predicate)

fun YSArray<String>.join(separator: String): String = joinToString(separator)

fun <T, R> YSArray<T>.reduce(f: (R, T) -> R, seed: R): R = fold(seed, f)

fun <T> YSArray<T>.slice(start: Int = 0, end: Int? = null): YSArray<T> {
    val length = size
    val realStart = if (start >= 0) start else max(0, length + start)
    val realEnd = end?.let { if (it >= 0) min(it, length) else length + it } ?: length
    return if (realStart < realEnd)
        slice(realStart until realEnd).toMutableList()
    else
        mutableListOf()
}

fun <T> YSArray<T>.sort(comparator: (T, T) -> Int): YSArray<T> = also { sortWith(Comparator { a, b -> comparator(a, b) }) }

fun <T> YSArray<T>.pop(): T? = if (isNotEmpty()) removeAt(this.size - 1) else null

fun <T> YSArray<T>.shift(): T? =
    if (isNotEmpty()) this.removeAt(0) else null

fun <T> YSArray<T>.unshift(item: T): Int {
    add(0, item)
    return size
}

fun <T> YSArray<T>.splice(from: Int, count: Int? = null): YSArray<T> {
    if (count != null && count <= 0) return mutableListOf()
    val realStart = if (from >= 0) {
        from
    } else {
        max(0, size + from)
    }
    if (realStart >= size) {
        return mutableListOf()
    }

    val assumedCount = size - realStart
    val numCount = min(count ?: assumedCount, assumedCount)
    if (numCount <= 0) {
        return mutableListOf()
    }
    val realEnd = realStart + numCount
    if (realStart >= realEnd) {
        return mutableListOf()
    }

    val toReturn = subList(realStart, realEnd).toMutableList()
    repeat(numCount) { removeAt(realStart) }
    return toReturn
}

fun <T, R> YSArray<T>.__flatMap(f: (T) -> YSArray<R>): YSArray<R> = flatMap(f).toMutableList()

fun <T> YSArray<T>.reverse(): YSArray<T> = this.asReversed()
