package com.yandex.pay.core.data

import com.yandex.xplat.yandex.pay.AuthMethods

/**
 * Defines what data the Payment Token will contain.
 */
enum class AuthMethod {
    /**
     * Payment Token will contain encrypted banking card details: FPAN, card number, etc.
     */
    PanOnly,

    /**
     * Payment Token will contain encrypted tokenized card details: DPAN and TAVV-cryptogram of the payment system.
     */
    CloudToken;

    internal val xplat: AuthMethods
        get() = when (this) {
            PanOnly -> AuthMethods.panOnly
            CloudToken -> AuthMethods.cloudToken
        }

    internal companion object {
        fun from(xplat: AuthMethods): AuthMethod = when (xplat) {
            AuthMethods.panOnly -> PanOnly
            AuthMethods.cloudToken -> CloudToken
        }
    }
}
