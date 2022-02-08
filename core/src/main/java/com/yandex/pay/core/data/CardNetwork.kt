package com.yandex.pay.core.data

import com.yandex.xplat.yandex.pay.CardNetworks
import com.yandex.xplat.yandex.pay.CardType

enum class CardNetwork {
    AmEx,
    Discover,
    JCB,
    MasterCard,
    Visa,
    MIR,
    UnionPay,
    UzCard,
    Maestro,
    VisaElectron;

    internal val xplat: CardNetworks
        get() = when (this) {
            AmEx -> CardNetworks.amex
            Discover -> CardNetworks.discover
            JCB -> CardNetworks.jcb
            MasterCard -> CardNetworks.masterCard
            Visa -> CardNetworks.visa
            MIR -> CardNetworks.mir
            UnionPay -> CardNetworks.unionPay
            UzCard -> CardNetworks.uzCard
            Maestro -> CardNetworks.maestro
            VisaElectron -> CardNetworks.visaElectron
        }

    internal companion object {
        fun from(xplat: CardNetworks): CardNetwork = when (xplat) {
            CardNetworks.amex -> AmEx
            CardNetworks.discover -> Discover
            CardNetworks.jcb -> JCB
            CardNetworks.masterCard -> MasterCard
            CardNetworks.visa -> Visa
            CardNetworks.mir -> MIR
            CardNetworks.unionPay -> UnionPay
            CardNetworks.uzCard -> UzCard
            CardNetworks.maestro -> Maestro
            CardNetworks.visaElectron -> VisaElectron
        }

        fun from(number: String): CardNetwork? =
            CardType.cardTypeFromCardNumber(number).cardNetwork?.let(::from)
    }
}
