package com.yandex.pay.core.state

import com.yandex.pay.core.data.CheckoutData
import com.yandex.pay.core.data.PaymentCheckoutResult
import com.yandex.pay.core.infra.State

internal data class CheckoutState(
    val checkoutData: CheckoutData?,
    val checkoutResult: PaymentCheckoutResult?
) : State {
    internal companion object {
        fun create(): CheckoutState = CheckoutState(null, null)
    }

    val checkingOut: Boolean get() = checkoutData != null && checkoutResult == null
    val checkedOut: Boolean get() = checkoutResult != null
}
