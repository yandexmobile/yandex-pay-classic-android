package com.yandex.pay.core.ui.views.presenters

import com.yandex.pay.core.data.CurrencyCode
import com.yandex.pay.core.data.Price
import com.yandex.pay.core.ui.views.interfaces.ICheckoutButtonView
import com.yandex.pay.core.ui.views.interfaces.Presenter
import com.yandex.pay.core.ui.views.interfaces.updating
import com.yandex.pay.core.utils.Formatter
import java.util.*

internal class CheckoutButtonPresenter(currencyCode: CurrencyCode, locale: Locale) :
    Presenter<CheckoutButtonPresenter.Payload, ICheckoutButtonView> {
    sealed interface Payload {
        class Normal(val price: Price, val disabled: Boolean, val onClick: () -> Unit) : Payload
        object CheckoutInProgress : Payload
        object Loading : Payload
        object CheckedOut : Payload
        object Error : Payload
    }

    private val priceFormatter: Formatter = Formatter.PriceFormatter(
        Currency.getInstance(currencyCode.toString()),
        locale,
    )

    override fun present(data: Payload, view: ICheckoutButtonView) {
        when (data) {
            is Payload.Normal -> present(data, view)
            is Payload.CheckoutInProgress -> present(data, view)
            is Payload.Loading -> present(data, view)
            is Payload.CheckedOut -> present(data, view)
            is Payload.Error -> present(data, view)
        }
    }

    private fun present(data: Payload.Normal, view: ICheckoutButtonView) {
        view.updating {
            state = ICheckoutButtonView.State.NORMAL
            disabled = data.disabled
            value = priceFormatter.format(data.price.value)
            onClick = data.onClick
        }
    }

    private fun present(data: Payload.Loading, view: ICheckoutButtonView) {
        view.updating {
            state = ICheckoutButtonView.State.LOADING
            disabled = true
            value = ""
            onClick = {}
        }
    }

    private fun present(data: Payload.CheckoutInProgress, view: ICheckoutButtonView) {
        view.updating {
            state = ICheckoutButtonView.State.CHECKING_OUT
            disabled = true
            value = ""
            onClick = {}
        }
    }

    private fun present(data: Payload.CheckedOut, view: ICheckoutButtonView) {
        view.updating {
            state = ICheckoutButtonView.State.CHECKED_OUT
            disabled = true
            value = ""
            onClick = {}
        }
    }

    private fun present(data: Payload.Error, view: ICheckoutButtonView) {
        view.updating {
            state = ICheckoutButtonView.State.ERROR
            disabled = true
            value = ""
            onClick = {}
        }
    }
}
