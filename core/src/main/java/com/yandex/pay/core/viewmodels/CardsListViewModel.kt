package com.yandex.pay.core.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.yandex.pay.core.actions.UserCardsAction
import com.yandex.pay.core.data.UserCard
import com.yandex.pay.core.events.Event
import com.yandex.pay.core.infra.Store
import com.yandex.pay.core.state.UserCardsState
import com.yandex.pay.core.ui.views.interfaces.ICardItemView
import com.yandex.pay.core.ui.views.interfaces.ICardsListView
import com.yandex.pay.core.ui.views.interfaces.IHeaderView
import com.yandex.pay.core.ui.views.presenters.CardItemPresenter
import com.yandex.pay.core.ui.views.presenters.CardsListPresenter
import com.yandex.pay.core.ui.views.presenters.HeaderPresenter

internal class CardsListViewModel(
    application: Application,
    private val parentViewModel: MainViewModel,
) : BaseViewModel(application) {
    private val store: Store
        get() = parentViewModel.store
    private val userCardsState: UserCardsState
        get() = store.state.userCards.value!!

    val selectedCardChanged: LiveData<Int> =
        store.state.userCards.map { it.selected }.distinctUntilChanged()

    private val headerPresenter: HeaderPresenter = HeaderPresenter()
    private val listPresenter: CardsListPresenter =
        CardsListPresenter(
            CardItemPresenter(
                application.resources,
                ICardItemView.AccessoryType.Checkmark
            )
        )

    fun updateHeader(header: IHeaderView) {
        headerPresenter.present(HeaderPresenter.Payload.BackButton(::closeWithBackButton), header)
    }

    fun updateCardsList(view: ICardsListView) {
        val (cards, selected) = userCardsState
        val payload =
            CardsListPresenter.Payload(cards.orEmpty(), selected, ::selectItem, ::selectAddNewItem)
        listPresenter.present(payload, view)
    }

    private fun closeWithBackButton() {
        logEvent(Event.CardSelectionCancelled)
        close()
    }

    private fun close() {
        parentViewModel.onBackPressed()
    }

    private fun selectItem(item: Int) {
        val card = userCardsState.cards!![item]
        logCardSelection(card)
        store.dispatch(UserCardsAction.SetDefault(card.id))
        close()
    }

    private fun selectAddNewItem() {
        parentViewModel.startNewCardBinding(true)
    }

    private fun logCardSelection(card: UserCard) {
        val newCard = with(userCardsState) {
            cards?.getOrNull(selected)?.let { it.id != card.id } ?: true
        }
        logEvent(Event.CardSelected(newCard))
    }

    internal class Factory(
        private val application: Application,
        private val parentViewModel: MainViewModel,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            takeIf { modelClass == CardsListViewModel::class.java }?.let {
                CardsListViewModel(application, parentViewModel)
            } as T
    }
}
