package com.yandex.xplat.common

import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random.Default.nextDouble

fun fatalError(message: String): Nothing {
    throw RuntimeException(message)
}

fun <T> undefinedToNull(item: T?): T? = item

fun <T> nullthrows(item: T?): T {
    if (item != null) {
        return item
    }
    throw RuntimeException("Got unexpected null")
}

fun <T : Number> int64(value: T): Long = when (value) {
    is Long -> value
    is Int -> value.toLong()
    is Byte -> value.toLong()
    else -> throw Error("Unsupported type in `int64` function: $value")
}

fun int32ToInt64(value: Int) = value.toLong()

fun int32ToDouble(value: Int) = value.toDouble()

fun int64ToInt32(value: Long) = value.toInt()

fun int64ToDouble(value: Long) = value.toDouble()

fun stringToInt32(value: String, radix: Int = 10) = value.toIntOrNull(radix)

fun stringToInt64(value: String, radix: Int = 10) = value.toLongOrNull(radix)

fun stringToDouble(value: String) = value.toDoubleOrNull()

fun int32ToString(value: Int) = value.toString()

fun int64ToString(value: Long) = value.toString()

fun doubleToString(value: Double) = value.toString()

fun doubleToInt32(value: Double) = value.toInt()

fun doubleToInt64(value: Double) = value.toLong()

fun booleanToInt32(value: Boolean) = if (value) 1 else 0

fun int32ToBoolean(value: Int) = value != 0

fun floorDouble(value: Double) = floor(value)

fun randomDouble() = nextDouble()

fun <T> setToArray(value: YSSet<T>): YSArray<T> = value.values.toMutableList()

fun <T> arrayToSet(value: YSArray<T>): YSSet<T> = YSSet(value)

fun <T> iterableToArray(value: Iterable<T>): YSArray<T> = value.toMutableList()

fun <T> iterableToSet(value: Iterable<T>): YSSet<T> = YSSet(value.toMutableSet())

@Suppress("UNCHECKED_CAST")
fun <T, U> cast(array: YSArray<T>): YSArray<U> = array.map { it as U }

fun <T> castToAny(value: T): Any = value as Any

fun String.split(separator: String): YSArray<String> =
    this.split(separator, ignoreCase = false, limit = Int.MAX_VALUE)
        .filter(String::isNotEmpty)

fun String.slice(start: Int = 0, end: Int? = null): String {
    val length = this.length
    val realStart = if (start >= 0) start else max(0, length + start)
    var realEnd = length
    if (end != null) {
        realEnd = if (end >= 0) min(end, length) else length + end
    }
    return if (realStart < realEnd)
        this.slice(realStart until realEnd)
    else
        ""
}

fun String.substring(start: Int = 0, end: Int? = null): String {
    val len = this.length
    val intStart = start
    val intEnd = end ?: len
    val finalStart = intStart.coerceIn(0, len)
    val finalEnd = intEnd.coerceIn(0, len)
    val from = min(finalStart, finalEnd)
    val to = max(finalStart, finalEnd)
    return this.slice(from until to)
}

fun String.substr(start: Int = 0, length: Int? = null): String {
    val size = this.length
    val intStart = if (start >= 0) start else max(size + start, 0)
    val end = length ?: Int.MAX_VALUE
    val resultLength = min(max(end, 0), size - intStart)
    if (resultLength <= 0) {
        return ""
    }

    return this.slice(intStart until intStart + resultLength)
}

fun String.search(regex: String): Int =
    regex.toRegex().find(this)?.range?.first ?: -1

fun String.match(regex: String): YSArray<String>? =
    regex.toRegex().find(this)?.groupValues?.toMutableList()

fun String.includes(substring: String): Boolean =
    this.contains(substring)

fun String.charCodeAt(i: Int): Int = this[i].toByte().toInt()

fun String.padStart(targetLength: Int, padString: String): String {
    if (this.length > targetLength) {
        return this
    }

    val prefixLength = targetLength - this.length
    var prefix = padString
    if (targetLength > padString.length) {
        prefix = padString.repeat(prefixLength / padString.length + 1)
    }
    prefix = prefix.slice(0, prefixLength)
    return prefix + this
}

object TypeSupport {
    fun isArray(value: Any): Boolean = value::class == YSArray::class
    fun isMap(value: Any): Boolean = value::class == YSMap::class

    fun isString(value: Any): Boolean = value is String
    fun asString(value: Any): String? = value as? String
    fun tryCastAsString(value: Any): String = asString(value) ?: throw YSError("Non String value: $value")

    fun isBoolean(value: Any): Boolean = value is Boolean
    fun asBoolean(value: Any): Boolean? = value as? Boolean
    fun tryCastAsBoolean(value: Any): Boolean = asBoolean(value) ?: throw YSError("Non Boolean value: $value")

    fun isInt32(value: Any): Boolean = value is Int
    fun asInt32(value: Any): Int? = when (value) {
        is Int -> value
        is Long -> value.toInt()
        is Double -> value.toInt()
        else -> value as? Int
    }

    fun tryCastAsInt32(value: Any): Int = asInt32(value) ?: throw YSError("Non Int32 value: $value")

    fun isInt64(value: Any): Boolean = value is Long
    fun asInt64(value: Any): Long? = when (value) {
        is Int -> value.toLong()
        is Long -> value
        is Double -> value.toLong()
        else -> value as? Long
    }

    fun tryCastAsInt64(value: Any): Long = asInt64(value) ?: throw YSError("Non Int64 value: $value")

    fun isDouble(value: Any): Boolean = value is Double
    fun asDouble(value: Any): Double? = when (value) {
        is Int -> value.toDouble()
        is Long -> value.toDouble()
        is Double -> value
        else -> null
    }

    fun tryCastAsDouble(value: Any): Double = asDouble(value) ?: throw YSError("Non Double value: $value")
}

public open class YSError(override val message: String, cause: Throwable? = null) : RuntimeException(message, cause)

typealias Parcelize = kotlinx.android.parcel.Parcelize
typealias Parcelable = android.os.Parcelable
