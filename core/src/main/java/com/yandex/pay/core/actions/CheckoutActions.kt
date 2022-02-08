package com.yandex.pay.core.actions

import com.yandex.pay.core.XPlatApi
import com.yandex.pay.core.data.CheckoutData
import com.yandex.pay.core.data.PaymentCheckoutResult
import com.yandex.pay.core.infra.Dispatch

internal sealed interface CheckoutActions : Action {
    class Checkout private constructor(val data: CheckoutData) : CheckoutActions {
        companion object {
            fun create(
                data: CheckoutData,
                api: XPlatApi,
                dispatch: Dispatch,
                errorProcessor: ((Throwable) -> Unit)?,
            ): Checkout {
                api.checkout(data.cardId, data.merchant, data.paymentSheet) { result ->
                    result.onSuccess { dispatch(Done(it)) }.onFailure { errorProcessor?.invoke(it) }
                }
                return Checkout(data)
            }
        }
    }

    class Done(val result: PaymentCheckoutResult) : CheckoutActions
}
