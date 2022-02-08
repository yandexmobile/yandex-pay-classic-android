package com.yandex.pay.core.data

import com.yandex.xplat.yandex.pay.UserCard as XUserCard

internal class UserCard private constructor(
    val id: CardID,
    val allowedAuthMethods: List<AuthMethod>,
    val issuerBank: String,
    val uid: Int,
    val cardNetwork: CardNetwork,
    val last4Digits: String,
    val cardArt: CardArt,
    val bin: String,
) {
    internal val xplat: XUserCard
        get() = XUserCard(
            id.value,
            null,
            allowedAuthMethods.map(AuthMethod::xplat).toMutableList(),
            issuerBank,
            uid,
            cardNetwork.xplat,
            last4Digits,
            cardArt.xplat,
            bin,
        )

    internal companion object {
        fun from(xplat: XUserCard): UserCard = UserCard(
            CardID.from(xplat.id),
            xplat.allowedAuthMethods.map(AuthMethod::from),
            xplat.issuerBank,
            xplat.uid,
            CardNetwork.from(xplat.cardNetwork),
            xplat.last4Digits,
            CardArt.from(xplat.cardArt),
            xplat.bin,
        )

        fun from(cardID: CardID, cardNumber: String): UserCard = UserCard(
            cardID,
            emptyList(),
            "",
            0,
            CardNetwork.from(cardNumber)!!,
            cardNumber.takeLast(4),
            CardArt.empty,
            cardNumber.take(6),
        )
    }
}
