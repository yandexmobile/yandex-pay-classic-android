package com.yandex.xplat.common

import java.text.SimpleDateFormat
import java.util.*

class YSDate(private val value: Long) {
    constructor(s: String) : this(parseDate(s))
    constructor() : this(now())

    fun getMonth(): Int = Calendar.getInstance().apply { time = Date(value) }.get(Calendar.MONTH)

    fun getFullYear(): Int = Calendar.getInstance().apply { time = Date(value) }.get(Calendar.YEAR)

    fun getDate(): Int = Calendar.getInstance().apply { time = Date(value) }.get(Calendar.DAY_OF_MONTH)

    fun getHours(): Int = Calendar.getInstance().apply { time = Date(value) }.get(Calendar.HOUR_OF_DAY)

    fun getMinutes(): Int = Calendar.getInstance().apply { time = Date(value) }.get(Calendar.MINUTE)

    fun getSeconds(): Int = Calendar.getInstance().apply { time = Date(value) }.get(Calendar.SECOND)

    companion object {
        private val format: SimpleDateFormat by lazy {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        }

        fun now() = System.currentTimeMillis()

        private fun parseDate(s: String): Long = format.parse(s).time
    }

    fun getDateValue(): Date = Date(value)

    fun getTime(): Long = value
}
