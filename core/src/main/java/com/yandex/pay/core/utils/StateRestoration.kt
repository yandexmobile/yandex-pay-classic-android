package com.yandex.pay.core.utils

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.yandex.pay.core.data.CardDetails
import com.yandex.pay.core.data.CardID
import com.yandex.pay.core.data.Error
import com.yandex.pay.core.data.OrderDetails
import com.yandex.pay.core.viewmodels.BindCardViewModel

internal object StateRestoration {
    fun loadOrderDetails(bundle: Bundle): OrderDetails? =
        bundle.getParcelable(Keys.ORDER_DETAILS.value)

    fun loadOrderDetails(intent: Intent): OrderDetails? =
        intent.getParcelableExtra(Keys.ORDER_DETAILS.value)

    fun saveOrderDetails(bundle: Bundle, value: OrderDetails) {
        bundle.putParcelable(Keys.ORDER_DETAILS.value, value)
    }

    fun saveOrderDetails(intent: Intent, value: OrderDetails) {
        intent.putExtra(Keys.ORDER_DETAILS.value, value)
    }

    fun loadAuthorizationData(bundle: Bundle): Boolean? =
        Keys.AUTH_DATA.value.takeIf(bundle::containsKey)?.let(bundle::getBoolean)

    fun saveAuthorizationData(bundle: Bundle, value: Boolean) {
        bundle.putBoolean(Keys.AUTH_DATA.value, value)
    }

    fun saveError(bundle: Bundle, value: Error) {
        bundle.putParcelable(Keys.ERROR.value, value)
    }

    fun saveError(bundle: Bundle, value: ErrorDescriptor) {
        bundle.putParcelable(Keys.ERROR.value, value)
    }

    inline fun <reified T : Parcelable> loadError(bundle: Bundle): T? =
        bundle.getParcelable(Keys.ERROR.value)

    fun saveCheckoutDetails(bundle: Bundle, value: CardID) {
        bundle.putParcelable(Keys.CARD_ID.value, value)
    }

    fun loadCheckoutDetails(bundle: Bundle): CardID? = bundle.getParcelable(Keys.CARD_ID.value)

    fun loadCardBindingState(bundle: Bundle): BindCardViewModel.State? =
        bundle.getString(Keys.CARD_BINDING_STATE.value, null)?.let(BindCardViewModel.State::valueOf)

    fun saveCardBindingState(bundle: Bundle, value: BindCardViewModel.State) {
        bundle.putString(Keys.CARD_BINDING_STATE.value, value.name)
    }

    fun loadCardDetails(bundle: Bundle): CardDetails? =
        bundle.getParcelable(Keys.CARD_DETAILS.value)

    fun saveCardDetails(bundle: Bundle, value: CardDetails) {
        bundle.putParcelable(Keys.CARD_DETAILS.value, value)
    }

    internal enum class Keys(val value: String) {
        ORDER_DETAILS("com.yandex.pay.OrderDetails"),
        AUTH_DATA("com.yandex.pay.AuthData"),
        ERROR("com.yandex.pay.Error"),
        CARD_ID("com.yandex.pay.CardID"),
        CARD_BINDING_STATE("com.yandex.pay.CardBindingState"),
        CARD_DETAILS("com.yandex.pay.CardDetails"),
    }
}
