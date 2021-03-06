// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM models/card-networks.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public enum class CardNetworks {
    amex,
    discover,
    jcb,
    masterCard,
    visa,
    mir,
    unionPay,
    uzCard,
    maestro,
    visaElectron,
}
public fun cardNetworkFromString(value: String): CardNetworks? {
    when (value.toUpperCase()) {
        "AMEX" -> {
            return CardNetworks.amex
        }
        "DISCOVER" -> {
            return CardNetworks.discover
        }
        "JCB" -> {
            return CardNetworks.jcb
        }
        "MASTERCARD" -> {
            return CardNetworks.masterCard
        }
        "VISA" -> {
            return CardNetworks.visa
        }
        "MIR" -> {
            return CardNetworks.mir
        }
        "UNIONPAY" -> {
            return CardNetworks.unionPay
        }
        "UZCARD" -> {
            return CardNetworks.uzCard
        }
        "MAESTRO" -> {
            return CardNetworks.maestro
        }
        "VISAELECTRON" -> {
            return CardNetworks.visaElectron
        }
        else -> {
            return null
        }
    }
}

public fun cardNetworkToString(value: CardNetworks): String {
    when (value) {
        CardNetworks.amex -> {
            return "AMEX"
        }
        CardNetworks.discover -> {
            return "DISCOVER"
        }
        CardNetworks.jcb -> {
            return "JCB"
        }
        CardNetworks.masterCard -> {
            return "MASTERCARD"
        }
        CardNetworks.visa -> {
            return "VISA"
        }
        CardNetworks.mir -> {
            return "MIR"
        }
        CardNetworks.unionPay -> {
            return "UNIONPAY"
        }
        CardNetworks.uzCard -> {
            return "UZCARD"
        }
        CardNetworks.maestro -> {
            return "MAESTRO"
        }
        CardNetworks.visaElectron -> {
            return "VISAELECTRON"
        }
    }
}

