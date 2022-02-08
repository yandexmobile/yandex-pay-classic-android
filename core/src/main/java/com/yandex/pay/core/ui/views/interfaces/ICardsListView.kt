package com.yandex.pay.core.ui.views.interfaces

internal interface ICardsListView: View {
    var cards: List<ICardItemView>
    var selected: Int
    var onCardClick: (Int) -> Unit
    var onNewCardClick: () -> Unit
}
