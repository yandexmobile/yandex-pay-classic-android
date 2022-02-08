package com.yandex.pay.core.data

import com.yandex.xplat.yandex.pay.CountryCodes

enum class CountryCode(internal val xplat: CountryCodes) {
    RU(CountryCodes.ru),
    US(CountryCodes.us);

    override fun toString(): String = when (this) {
        RU -> "ru"
        US -> "us"
    }

    internal companion object {
        fun from(xplat: CountryCodes): CountryCode = when (xplat) {
            CountryCodes.ru -> RU
            CountryCodes.us -> US
        }
    }
}
