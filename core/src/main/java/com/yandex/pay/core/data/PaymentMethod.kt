package com.yandex.pay.core.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.yandex.xplat.yandex.pay.PaymentMethod as XPaymentMethod

@Parcelize
class PaymentMethod(
    val allowedAuthMethods: List<AuthMethod>,
    val type: PaymentMethodType,
    val gateway: Gateway,
    val allowedCardNetworks: List<CardNetwork>,
    val gatewayMerchantId: GatewayMerchantID,
) : Parcelable {
    internal val xplat: XPaymentMethod
        get() = XPaymentMethod(
            allowedAuthMethods.map(AuthMethod::xplat).toMutableList(),
            type.xplat,
            gateway.value,
            allowedCardNetworks.map(CardNetwork::xplat).toMutableList(),
            gatewayMerchantId.value,
        )

    internal companion object {
        fun from(xplat: XPaymentMethod): PaymentMethod =
            PaymentMethod(
                xplat.allowedAuthMethods.map(AuthMethod::from),
                PaymentMethodType.from(xplat.type),
                Gateway.from(xplat.gateway),
                xplat.allowedCardNetworks.map(CardNetwork::from),
                GatewayMerchantID.from(xplat.gatewayMerchantId),
            )
    }
}
