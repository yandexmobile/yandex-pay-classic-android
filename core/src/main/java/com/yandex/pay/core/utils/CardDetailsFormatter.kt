package com.yandex.pay.core.utils

import android.content.res.Resources
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.yandex.pay.core.R
import com.yandex.pay.core.data.CardNetwork
import com.yandex.xplat.yandex.pay.CardType
import com.yandex.xplat.yandex.pay.applySpacers

internal class CardDetailsFormatter(val context: Context) {
    enum class Context {
        PAY_BUTTON, CARD_LIST_ITEM, CARD_NUMBER_COLLAPSED_INPUT,
    }

    @DrawableRes
    fun getCardNetworkImage(value: CardNetwork): Int = when (value) {
        CardNetwork.AmEx -> R.drawable.yandexpay_ic_american_express
        CardNetwork.Discover -> R.drawable.yandexpay_ic_discover
        CardNetwork.JCB -> R.drawable.yandexpay_ic_jcb
        CardNetwork.MasterCard -> R.drawable.yandexpay_ic_mastercard
        CardNetwork.Visa -> R.drawable.yandexpay_ic_visa
        CardNetwork.MIR -> R.drawable.yandexpay_ic_mir
        CardNetwork.UnionPay -> R.drawable.yandexpay_ic_union_pay
        CardNetwork.UzCard -> R.drawable.yandexpay_ic_uzcard
        CardNetwork.Maestro -> R.drawable.yandexpay_ic_maestro
        CardNetwork.VisaElectron -> R.drawable.yandexpay_ic_visa_electron
    }

    @StringRes
    fun getCardNetworkName(value: CardNetwork): Int = when (value) {
        CardNetwork.AmEx -> R.string.yandexpay_card_network_amex
        CardNetwork.Discover -> R.string.yandexpay_card_network_discover
        CardNetwork.JCB -> R.string.yandexpay_card_network_jcb
        CardNetwork.MasterCard -> R.string.yandexpay_card_network_masterCard
        CardNetwork.Visa -> R.string.yandexpay_card_network_visa
        CardNetwork.MIR -> R.string.yandexpay_card_network_mir
        CardNetwork.UnionPay -> R.string.yandexpay_card_network_unionPay
        CardNetwork.UzCard -> R.string.yandexpay_card_network_uzCard
        CardNetwork.Maestro -> R.string.yandexpay_card_network_maestro
        CardNetwork.VisaElectron -> R.string.yandexpay_card_network_visaElectron
    }

    fun getCardNetworkName(resources: Resources, value: CardNetwork): String =
        resources.getString(getCardNetworkName(value))

    @StringRes
    fun getCardNumberTitleTemplate(): Int = when (context) {
        Context.PAY_BUTTON -> R.string.yandexpay_pay_button_card_number
        Context.CARD_LIST_ITEM -> R.string.yandexpay_card_number
        Context.CARD_NUMBER_COLLAPSED_INPUT -> R.string.yandexpay_collapsed_input_card_number
    }

    fun getCardNumberTitle(resources: Resources, number: String): String =
        resources.getString(getCardNumberTitleTemplate(), number.takeLast(4))

    fun formatTextFieldContents(input: String): String? {
        val filteredInput = input.filter(Char::isDigit)
        val cardType = CardType.cardTypeFromCardNumber(filteredInput)
        return applyFormatting(filteredInput, cardType)
    }

    fun applyFormatting(inputValue: String, cardType: CardType): String? =
        cardType.takeIf { inputValue.length <= it.validLengths.last() }
            ?.let { applySpacers(inputValue, it.spacers) }
}
