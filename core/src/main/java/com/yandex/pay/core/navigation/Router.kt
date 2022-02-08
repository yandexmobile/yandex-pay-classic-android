package com.yandex.pay.core.navigation

import com.yandex.pay.core.data.Error
import com.yandex.pay.core.data.PaymentCheckoutResult

internal interface Router {
    fun push(route: Route)
    fun pull(toRoute: Route? = null)
    fun replace(route: Route)
    fun finish(result: PaymentCheckoutResult?)
    fun finishWithError(error: Error)
}
