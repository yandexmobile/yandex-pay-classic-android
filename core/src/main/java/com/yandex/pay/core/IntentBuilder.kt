package com.yandex.pay.core

import android.content.Context
import android.content.Intent
import com.yandex.pay.core.data.*
import com.yandex.pay.core.navigation.ActivityRoutes
import com.yandex.pay.core.utils.StateRestoration

class IntentBuilder private constructor(
    private val intent: Intent,
) {
    private var orderDetails: OrderDetails? = null

    /**
     * Fills the intent builder with [com.yandex.pay.core.data.OrderDetails] that are required to run the Y.Pay.
     */
    fun setOrderDetails(value: OrderDetails): IntentBuilder = also {
        orderDetails = value
    }

    /**
     * Fills the intent builder with params to build [com.yandex.pay.core.data.OrderDetails] that are required to run the Y.Pay.
     * @see [setOrderDetails]
     */
    fun setOrderDetails(
        merchant: Merchant,
        order: Order,
        paymentMethods: List<PaymentMethod>,
        currencyCode: CurrencyCode = OrderDetails.defaultCurrencyCode,
        countryCode: CountryCode = OrderDetails.defaultCountryCode,
    ): IntentBuilder = setOrderDetails(OrderDetails.builder()
        .setMerchant(merchant)
        .setOrder(order)
        .setCurrencyCode(currencyCode)
        .setCountryCode(countryCode)
        .setPaymentMethods(paymentMethods)
        .build()
    )

    /**
     * Builds an [Intent] to run with [android.app.Activity.startActivityForResult].
     */
    fun build(): Intent {
        val orderDetails =
            requireNotNull(this.orderDetails) { "Order details must be provided to build Intent" }
        return intent.apply {
            flags =
                flags or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP
            StateRestoration.saveOrderDetails(this, orderDetails)
        }
    }

    internal companion object {
        internal fun create(context: Context): IntentBuilder =
            IntentBuilder(ActivityRoutes.mainActivityIntent(context))
    }
}
