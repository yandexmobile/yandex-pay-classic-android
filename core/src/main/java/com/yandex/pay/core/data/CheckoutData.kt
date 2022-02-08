package com.yandex.pay.core.data

internal class CheckoutData(
    val cardId: CardID,
    val merchant: Merchant,
    val paymentSheet: PaymentSheet,
)
