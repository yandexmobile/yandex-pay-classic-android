package com.yandex.pay.core.middleware

import com.yandex.pay.core.actions.Action
import com.yandex.pay.core.actions.NavigationAction
import com.yandex.pay.core.actions.UserCardsAction
import com.yandex.pay.core.events.Event
import com.yandex.pay.core.events.YPayMetrica
import com.yandex.pay.core.infra.Dispatch
import com.yandex.pay.core.infra.Middleware
import com.yandex.pay.core.infra.Next
import com.yandex.pay.core.navigation.Route
import com.yandex.pay.core.state.AppState

internal class UserCardsMiddleware(private val metrica: YPayMetrica) : Middleware {
    override fun handle(state: AppState, action: Action, next: Next, dispatch: Dispatch): Action =
        when (action) {
            is UserCardsAction.StartNewCardBinding -> {
                metrica.log(Event.NewCardBindingStarted(action.fromCardsList))
                next(state, NavigationAction.Push(Route.NewCardNumberBinding))
            }
            is UserCardsAction.Require3DS -> {
                metrica.log(Event.NewCardBinding3DSRequired)
                next(state, NavigationAction.Push(Route.Confirm3DS(action.uri)))
                action
            }
            is UserCardsAction.Hide3DS -> {
                if (action.wasShown) {
                    val nextAction = if (action.success) {
                        NavigationAction.Pull(Route.GetCards)
                    } else {
                        NavigationAction.Pull()
                    }
                    next(state, nextAction)
                    action
                } else {
                    action
                }
            }
            is UserCardsAction.CancelBinding -> {
                metrica.log(Event.NewCardBindingCancelled)
                next(state, action)
            }
            is UserCardsAction.CompleteCardBinding -> {
                metrica.log(Event.NewCardBindingComplete)
                next(state, action)
            }
            else -> next(state, action)
        }
}
