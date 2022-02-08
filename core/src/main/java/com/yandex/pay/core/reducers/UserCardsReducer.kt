package com.yandex.pay.core.reducers

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.actions.UserCardsAction
import com.yandex.pay.core.infra.Reducer
import com.yandex.pay.core.state.AppState
import com.yandex.pay.core.state.UserCardsState

internal class UserCardsReducer : Reducer {
    override fun reduce(state: AppState, action: Action): AppState =
        when (action) {
            is UserCardsAction.GetCards -> if (action.withValidation) {
                state.with(
                    userCards = state.cards.copy(loading = true),
                    orderDetails = state.orderDetails.value!!.copy(validating = true)
                )
            } else {
                state.withUserCards(state.cards.copy(loading = true))
            }
            is UserCardsAction.CardsLoaded -> state.withUserCards(
                state.cards.copy(
                    cards = action.items,
                    selected = 0,
                    loading = false,
                )
            )
            is UserCardsAction.CardsLoadedWithSelection -> state.withUserCards(
                state.cards.copy(
                    cards = action.items,
                    selected = action.selected,
                    loading = false,
                )
            )
            is UserCardsAction.SetDefault -> state.withUserCards(state.cards.copy(
                selected = state.cards.cards?.indexOfFirst { it.id == action.cardId }
                    ?: UserCardsState.NO_SELECTED,
            ))
            is UserCardsAction.BindCard -> state.withCardBindingState(
                state.cardBindingState.value!!.copy(
                    cardDetails = action.card,
                    show3DS = null,
                )
            )
            is UserCardsAction.CompleteCardBinding -> {
                val newCards = state.cards.cards?.plus(action.card) ?: listOf(action.card)
                state.with(
                    cardBindingState = state.cardBindingState.value!!.copy(
                        cardDetails = null,
                        show3DS = null,
                    ),
                    userCards = state.cards.copy(
                        cards = newCards,
                    ),
                )
            }
            is UserCardsAction.Require3DS -> state.withCardBindingState(
                state.cardBindingState.value!!.copy(
                    show3DS = action.uri,
                )
            )
            is UserCardsAction.Hide3DS -> state.withCardBindingState(
                state.cardBindingState.value!!.copy(show3DS = null),
            )
            is UserCardsAction.CancelBinding, UserCardsAction.FailCardBinding -> state.withCardBindingState(
                state.cardBindingState.value!!.copy(cardDetails = null, show3DS = null),
            )
            else -> state
        }
}

private val AppState.cards: UserCardsState
    get() = userCards.value!!
