package com.yandex.pay.core.middleware

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.actions.UserCardsAction
import com.yandex.pay.core.data.CardID
import com.yandex.pay.core.data.UserCard
import com.yandex.pay.core.infra.Dispatch
import com.yandex.pay.core.infra.Middleware
import com.yandex.pay.core.infra.Next
import com.yandex.pay.core.state.AppState
import com.yandex.pay.core.storage.CurrentCardChanger

internal class CurrentCardMiddleware(private val currentCardChanger: CurrentCardChanger) :
    Middleware {
    override fun handle(state: AppState, action: Action, next: Next, dispatch: Dispatch): Action {
        val newAction = when (action) {
            is UserCardsAction.CardsLoaded -> UserCardsAction.CardsLoadedWithSelection(
                action.items,
                findUserCardIndex(action.items, currentCardChanger.change(action.items)) ?: 0
            )
            is UserCardsAction.SetDefault -> action.also {
                currentCardChanger.changeTo(state.userCards.value?.cards?.find { it.id == action.cardId })
            }
            else -> action
        }
        return next(state, newAction)
    }

    private fun findUserCardIndex(items: List<UserCard>, cardID: CardID?): Int? =
        if (cardID == null || items.isEmpty()) null
        else items.indexOfFirst { it.id == cardID }.takeUnless { it == -1 }
}
