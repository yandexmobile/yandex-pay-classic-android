package com.yandex.pay.core.data

import com.yandex.xplat.yandex.pay.CurrencyCodes

enum class CurrencyCode(internal val xplat: CurrencyCodes) {
    RUB(CurrencyCodes.rub),
    USD(CurrencyCodes.usd);

    override fun toString(): String = when (this) {
        RUB -> "rub"
        USD -> "usd"
    }

    internal companion object {
        fun from(xplat: CurrencyCodes): CurrencyCode = when (xplat) {
            CurrencyCodes.rub -> RUB
            CurrencyCodes.usd -> USD
        }
    }
}
