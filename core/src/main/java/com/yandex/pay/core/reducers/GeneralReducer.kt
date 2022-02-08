package com.yandex.pay.core.reducers

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.actions.GeneralActions
import com.yandex.pay.core.infra.Reducer
import com.yandex.pay.core.state.AppState

internal class GeneralReducer : Reducer {
    override fun reduce(state: AppState, action: Action): AppState = when (action) {
        is GeneralActions.SetError -> state.with(
            generalState = state.generalState.value!!.copy(
                error = action.error,
            ),
            userCards = state.userCards.value!!.copy(
                loading = false,
            ),
            authorization = state.authorization.value!!.copy(
                loading = false,
            ),
            checkoutState = state.checkoutState.value!!.copy(
                checkoutResult = null,
            ),
            orderDetails = state.orderDetails.value!!.copy(
                validating = false,
            ),
            cardBindingState = state.cardBindingState.value!!.copy(
                cardDetails = null,
                show3DS = null,
            ),
        )
        is GeneralActions.ResetLoading -> state.with(
            checkoutState = state.checkoutState.value!!.copy(
                checkoutData = null,
                checkoutResult = null,
            ),
            orderDetails = state.orderDetails.value!!.copy(
                validating = false,
            ),
            userCards = state.userCards.value!!.copy(
                loading = false,
            ),
            cardBindingState = state.cardBindingState.value!!.copy(
                cardDetails = null,
                show3DS = null,
            ),
        )
        is GeneralActions.RecoverFromError -> state.with(
            generalState = state.generalState.value!!.copy(
                error = null,
            ),
            checkoutState = state.checkoutState.value!!.copy(
                checkoutData = null,
                checkoutResult = null,
            ),
            cardBindingState = state.cardBindingState.value!!.copy(
                cardDetails = null,
                show3DS = null,
            ),
        )
        else -> state
    }
}
