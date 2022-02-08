package com.yandex.pay.core.data

internal class LastUsedCard(val cardID: CardID, val network: CardNetwork, val last4Digits: String) {
    internal companion object {
        fun from(value: UserCard): LastUsedCard =
            LastUsedCard(value.id, value.cardNetwork, value.last4Digits)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other?.javaClass) {
            return false
        }

        other as LastUsedCard
        return cardID == other.cardID && network == other.network && last4Digits == other.last4Digits
    }

    override fun hashCode(): Int = intArrayOf(
        cardID.hashCode(),
        network.hashCode(),
        last4Digits.hashCode()
    ).fold(0) { result, next -> result * 31 + next }
}
