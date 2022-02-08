package com.yandex.pay.core.actions

import com.yandex.pay.core.data.Error
import com.yandex.pay.core.data.PaymentCheckoutResult
import com.yandex.pay.core.navigation.Route
import com.yandex.pay.core.navigation.Router

internal sealed interface NavigationAction : Action {
    class SetRouter(val router: Router) : NavigationAction

    class Push(val route: Route) : NavigationAction
    class Pull(val toRoute: Route? = null) : NavigationAction
    class Replace(val route: Route) : NavigationAction

    class Complete(val checkoutResult: PaymentCheckoutResult?) : NavigationAction
    class CompleteWithError(val error: Error) : NavigationAction
}
