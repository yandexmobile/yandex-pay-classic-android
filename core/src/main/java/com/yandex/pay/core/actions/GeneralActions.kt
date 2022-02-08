package com.yandex.pay.core.actions

import com.yandex.pay.core.data.ErrorType
import com.yandex.pay.core.data.OrderDetails

internal sealed interface GeneralActions : Action {
    object KickOff : GeneralActions
    class ConfirmOrderDetailsValidated(val details: OrderDetails) : GeneralActions
    object ResetLoading : GeneralActions
    class SetError(val error: ErrorType) : GeneralActions
    object RecoverFromError : GeneralActions
}
