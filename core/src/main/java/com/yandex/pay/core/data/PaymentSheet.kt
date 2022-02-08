package com.yandex.pay.core.data

import com.yandex.xplat.yandex.pay.PaymentSheet as XPaymentSheet

class PaymentSheet(
    val merchant: Merchant,
    val order: Order,
    val currencyCode: CurrencyCode,
    val countryCode: CountryCode,
    val paymentMethods: List<PaymentMethod>,
) {
    internal val xplat: XPaymentSheet
        get() = XPaymentSheet(
            merchant.xplat,
            order.xplat,
            currencyCode.xplat,
            countryCode.xplat,
            paymentMethods.map(PaymentMethod::xplat).toMutableList(),
        )

    internal companion object {
        fun from(xplat: XPaymentSheet): PaymentSheet = PaymentSheet(
            Merchant.from(xplat.merchant, ""),
            Order.from(xplat.order),
            CurrencyCode.from(xplat.currencyCode),
            CountryCode.from(xplat.countryCode),
            xplat.paymentMethods.map(PaymentMethod::from),
        )
    }
}
