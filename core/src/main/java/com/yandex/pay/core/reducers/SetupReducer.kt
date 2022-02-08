package com.yandex.pay.core.reducers

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.actions.GeneralActions
import com.yandex.pay.core.infra.Reducer
import com.yandex.pay.core.state.AppState

internal class SetupReducer : Reducer {
    override fun reduce(state: AppState, action: Action): AppState = when (action) {
        is GeneralActions.ConfirmOrderDetailsValidated -> state.withOrderDetails(
            state.orderDetails.value!!.copy(orderDetails = action.details, validating = false),
        )
        else -> state
    }
}
