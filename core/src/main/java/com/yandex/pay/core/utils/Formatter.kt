package com.yandex.pay.core.utils

import java.text.NumberFormat
import java.util.*

internal sealed interface Formatter {
    fun format(value: String): String

    class PriceFormatter(private val currency: Currency, private val locale: Locale): Formatter {
        override fun format(value: String): String {
            return value.toBigDecimalOrNull()?.let {
                val formatCurrency = NumberFormat.getCurrencyInstance(locale)
                formatCurrency.currency = currency
                formatCurrency.format(it)
            } ?: value
        }
    }
}
