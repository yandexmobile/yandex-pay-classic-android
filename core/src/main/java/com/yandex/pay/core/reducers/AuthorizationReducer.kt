package com.yandex.pay.core.reducers

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.actions.AuthorizationAction
import com.yandex.pay.core.data.OAuthToken
import com.yandex.pay.core.infra.Reducer
import com.yandex.pay.core.state.AppState

internal class AuthorizationReducer : Reducer {
    override fun reduce(state: AppState, action: Action): AppState = when (action) {
        is AuthorizationAction.Authorize -> state.withAuthorization(
            state.authorization.value!!.copy(
                loading = true
            )
        )
        is AuthorizationAction.StoreToken -> state.withAuthorization(
            state.authorization.value!!.copy(
                token = action.token,
                loading = false
            )
        )
        is AuthorizationAction.Complete -> state
        is AuthorizationAction.Cancel -> state.withAuthorization(
            state.authorization.value!!.copy(
                loading = false
            )
        )
        is AuthorizationAction.InvalidateStoredToken -> state.with(
            authorization = state.authorization.value!!.copy(
                loading = false,
                token = OAuthToken.empty()
            ),
            userCards = state.userCards.value!!.copy(
                loading = false,
                cards = null,
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
