package com.yandex.pay.core.ui.views.presenters

import com.yandex.pay.core.data.UserCard
import com.yandex.pay.core.ui.views.interfaces.ICardItemView
import com.yandex.pay.core.ui.views.interfaces.ICardsListView
import com.yandex.pay.core.ui.views.interfaces.Presenter

internal class CardsListPresenter(private val cardItemPresenter: CardItemPresenter) :
    Presenter<CardsListPresenter.Payload, ICardsListView> {
    class Payload(
        val cards: List<UserCard>,
        val selected: Int,
        val onCardClick: (Int) -> Unit,
        val onNewCardClick: () -> Unit,
    )

    @Suppress("MoveLambdaOutsideParentheses")
    override fun present(data: Payload, view: ICardsListView) {
        val selected = data.selected
        view.cards = data.cards.mapIndexed { index, card ->
            CardItemView().also {
                cardItemPresenter.present(
                    CardItemPresenter.Payload.Card(
                        card,
                        selected == index,
                        { data.onCardClick(index) },
                    ),
                    it,
                )
            }
        }
        view.selected = selected
        view.onCardClick = data.onCardClick
        view.onNewCardClick = data.onNewCardClick
    }

    private class CardItemView : ICardItemView {
        override var loading: Boolean = false
        override var accessoryType: ICardItemView.AccessoryType = ICardItemView.AccessoryType.None
        override var title: String = ""
        override var subtitle: String = ""
        override var image: Int? = null
        override var onClick: () -> Unit = {}

        override fun update() = Unit
    }
}
