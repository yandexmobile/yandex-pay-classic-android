package com.yandex.pay.core.reducers

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.actions.CheckoutActions
import com.yandex.pay.core.infra.Reducer
import com.yandex.pay.core.state.AppState

internal class CheckoutReducer : Reducer {
    override fun reduce(state: AppState, action: Action): AppState = when (action) {
        is CheckoutActions.Checkout -> state.withCheckoutState(
            state.checkoutState.value!!.copy(
                checkoutData = action.data,
                checkoutResult = null,
            )
        )
        is CheckoutActions.Done -> state.withCheckoutState(
            state.checkoutState.value!!.copy(
                checkoutResult = action.result,
            )
        )
        else -> state
    }
}
