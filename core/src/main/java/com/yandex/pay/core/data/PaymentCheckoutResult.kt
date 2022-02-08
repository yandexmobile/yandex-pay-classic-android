package com.yandex.pay.core.data

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import com.yandex.xplat.yandex.pay.PayCheckoutResponse
import kotlinx.parcelize.Parcelize

/**
 * The result of Y.Pay activity run.
 */
@Parcelize
class PaymentCheckoutResult private constructor(
    /**
     * The Payment token to be passed to PSP.
     */
    val paymentToken: PaymentToken,
    /**
     * The Payment type a user selected. Currently, it's Card only.
     */
    val paymentMethodType: PaymentMethodType,
    /**
     * Last four digits of the selected card to pay with.
     */
    val lastFourDigits: String?,
    /**
     * Card network of the selected card.
     */
    val cardNetwork: CardNetwork?,
) : Parcelable {
    internal fun addToIntent(intent: Intent) {
        intent.putExtra(CHECKOUT_RESULT_KEY, this)
    }

    internal companion object {
        const val RESULT_ERROR_CODE = Activity.RESULT_FIRST_USER
        private const val CHECKOUT_RESULT_KEY = "PAYMENT_CHECKOUT_RESULT"
        private const val ERROR_KEY = "ERROR_RESULT"

        fun isErrorResult(intent: Intent): Boolean =
            intent.hasExtra(ERROR_KEY)

        fun extractError(intent: Intent): Error? =
            intent.getParcelableExtra(ERROR_KEY)

        fun from(intent: Intent): PaymentCheckoutResult? =
            intent.getParcelableExtra(CHECKOUT_RESULT_KEY)

        fun from(response: PayCheckoutResponse): PaymentCheckoutResult =
            PaymentCheckoutResult(
                PaymentToken.from(response.paymentToken),
                PaymentMethodType.from(response.paymentMethodType),
                response.paymentMethodCardLastDigits,
                response.paymentCardNetwork?.let(CardNetwork::from)
            )

        fun addErrorToIntent(intent: Intent, error: Error) {
            intent.putExtra(ERROR_KEY, error)
        }
    }
}
