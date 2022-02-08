package com.yandex.pay.core.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Payment Token to use with Payment Service Provider. Use [toString] to get its String representation.
 */
@Parcelize
class PaymentToken private constructor(internal val value: String) : Parcelable {
    internal companion object {
        fun from(value: String): PaymentToken = PaymentToken(value)
    }

    /**
     * Gets Payment Token String representation to be used with Payment Service Providers.
     */
    override fun toString(): String = value

    override fun equals(other: Any?): Boolean = other is PaymentToken && value == other.value

    override fun hashCode(): Int = value.hashCode()
}
