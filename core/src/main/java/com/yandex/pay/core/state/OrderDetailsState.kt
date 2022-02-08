package com.yandex.pay.core.state

import com.yandex.pay.core.data.OrderDetails
import com.yandex.pay.core.infra.State

internal data class OrderDetailsState(
    val orderDetails: OrderDetails?,
    val validating: Boolean,
) : State {
    internal companion object {
        fun create(): OrderDetailsState = OrderDetailsState(null, false)
    }
}
