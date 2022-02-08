package com.yandex.pay.core.infra

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.state.AppState

internal class Store(
    initial: AppState,
    private val reducers: List<Reducer>,
    private val middlewares: List<Middleware>,
) : LiveData<Store>(), Dispatch {
    var state: AppState = initial
        private set

    @MainThread
    fun dispatch(action: Action) {
        val current = state
        val newAction = applyMiddleware(0)(current, action)
        val newState = applyReducer(current, newAction)
        state = newState
    }

    @MainThread
    override fun invoke(action: Action) = dispatch(action)

    @MainThread
    private fun applyMiddleware(index: Int): Next {
        if (index == middlewares.size) {
            return Next { _, action -> action }
        }
        return Next { state, action ->
            middlewares[index].handle(
                state,
                action,
                applyMiddleware(index + 1),
                ::dispatch,
            )
        }
    }

    @MainThread
    private fun applyReducer(state: AppState, action: Action): AppState =
        reducers.fold(state) { newState, reducer ->
            reducer.reduce(newState, action)
        }
}
