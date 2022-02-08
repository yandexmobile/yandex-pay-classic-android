package com.yandex.pay.core.ui.views.presenters

import android.content.res.Resources
import com.yandex.pay.core.R
import com.yandex.pay.core.data.UserCard
import com.yandex.pay.core.ui.views.interfaces.ICardItemView
import com.yandex.pay.core.ui.views.interfaces.Presenter
import com.yandex.pay.core.ui.views.interfaces.updating
import com.yandex.pay.core.utils.CardDetailsFormatter

internal class CardItemPresenter(
    private val resources: Resources,
    private val markType: ICardItemView.AccessoryType,
) : Presenter<CardItemPresenter.Payload, ICardItemView> {
    sealed interface Payload {
        object Loading : Payload
        class Card(val card: UserCard, val marked: Boolean, val onClick: (UserCard) -> Unit) :
            Payload

        class AddItem(val onClick: () -> Unit) : Payload
    }

    private val formatter: CardDetailsFormatter =
        CardDetailsFormatter(CardDetailsFormatter.Context.CARD_LIST_ITEM)

    override fun present(data: Payload, view: ICardItemView) = when (data) {
        is Payload.Loading -> present(data, view)
        is Payload.Card -> present(data, view)
        is Payload.AddItem -> present(data, view)
    }

    private fun present(data: Payload.Card, view: ICardItemView) {
        val card = data.card
        view.updating {
            loading = false
            title = buildTitle(card)
            subtitle = formatter.getCardNumberTitle(resources, card.last4Digits)
            accessoryType = if (data.marked) markType else ICardItemView.AccessoryType.None
            image = formatter.getCardNetworkImage(card.cardNetwork)
            onClick = { data.onClick(card) }
        }
    }

    private fun present(data: Payload.AddItem, view: ICardItemView) {
        view.updating {
            loading = false
            title = resources.getString(R.string.yandexpay_add_new_card_title)
            subtitle = ""
            accessoryType = ICardItemView.AccessoryType.None
            image = R.drawable.yandexpay_ic_add_new_card_image
            onClick = data.onClick
        }
    }

    private fun present(data: Payload.Loading, view: ICardItemView) {
        view.updating {
            loading = true
            onClick = { }
        }
    }

    private fun buildTitle(card: UserCard): String = resources.getString(
        R.string.yandexpay_card_title,
        formatter.getCardNetworkName(resources, card.cardNetwork)
    )
}
