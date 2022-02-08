package com.yandex.pay.core.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class OrderDetails(
    val merchant: Merchant,
    val order: Order,
    val paymentMethods: List<PaymentMethod>,
    val currencyCode: CurrencyCode = defaultCurrencyCode,
    val countryCode: CountryCode = defaultCountryCode,
) : Parcelable {
    class Builder internal constructor() {
        private var merchant: Merchant? = null
        private var order: Order? = null
        private var paymentMethods: List<PaymentMethod>? = null
        private var currencyCode: CurrencyCode = defaultCurrencyCode
        private var countryCode: CountryCode = defaultCountryCode

        fun setMerchant(value: Merchant): Builder = also {
            merchant = value
        }

        fun setMerchant(id: MerchantID, name: String, origin: String): Builder =
            setMerchant(Merchant(id, name, origin))

        fun setOrder(value: Order): Builder = also {
            order = value
        }

        fun setOrder(id: OrderID, amount: Price, label: String?, items: List<OrderItem>): Builder =
            setOrder(Order(id, amount, label, items))

        fun setCurrencyCode(value: CurrencyCode): Builder = also {
            currencyCode = value
        }

        fun setCountryCode(value: CountryCode): Builder = also {
            countryCode = value
        }

        fun setPaymentMethods(value: List<PaymentMethod>): Builder = also {
            paymentMethods = value
        }

        fun build(): OrderDetails {
            val merchant =
                requireNotNull(this.merchant) { "Merchant must be set to build OrderDetails" }
            val order = requireNotNull(this.order) { "Order must be set to build OrderDetails" }
            val paymentMethods =
                requireNotNull(this.paymentMethods) { "Payment methods must be set to build OrderDetails" }
            return OrderDetails(
                merchant,
                order,
                paymentMethods,
                currencyCode,
                countryCode,
            )
        }
    }

    val paymentSheet: PaymentSheet
        get() = PaymentSheet(
            merchant,
            order,
            currencyCode,
            countryCode,
            paymentMethods,
        )

    companion object {
        internal val defaultCurrencyCode: CurrencyCode = CurrencyCode.RUB
        internal val defaultCountryCode: CountryCode = CountryCode.RU

        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
