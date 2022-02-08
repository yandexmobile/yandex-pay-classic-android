package com.yandex.pay.core.data

import com.yandex.xplat.yandex.pay.PaymentMethodTypes

enum class PaymentMethodType {
    Card;

    internal val xplat: PaymentMethodTypes
        get() = when (this) {
            Card -> PaymentMethodTypes.card
        }

    internal companion object {
        fun from(xplat: PaymentMethodTypes): PaymentMethodType = when (xplat) {
            PaymentMethodTypes.card -> Card
        }
    }
}
