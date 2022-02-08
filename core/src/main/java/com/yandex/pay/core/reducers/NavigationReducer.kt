package com.yandex.pay.core.reducers

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.actions.NavigationAction
import com.yandex.pay.core.infra.Reducer
import com.yandex.pay.core.state.AppState

internal class NavigationReducer : Reducer {
    override fun reduce(state: AppState, action: Action): AppState =
        when (action) {
            is NavigationAction.SetRouter -> state.withNavigation(
                state.navigation.value!!.copy(
                    router = action.router,
                )
            )
            is NavigationAction.Push, is NavigationAction.Pull, is NavigationAction.Replace -> state // Handled by middleware
            is NavigationAction.Complete, is NavigationAction.CompleteWithError -> state // Handled by middleware
            else -> state
        }
}
