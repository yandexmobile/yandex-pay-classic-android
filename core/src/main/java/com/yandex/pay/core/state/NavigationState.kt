package com.yandex.pay.core.state

import com.yandex.pay.core.infra.State
import com.yandex.pay.core.navigation.Route
import com.yandex.pay.core.navigation.Router

internal data class NavigationState(
    val router: Router?,
    val route: Route?,
) : State {
    internal companion object {
        fun create(): NavigationState = NavigationState(null, null)
    }
}
