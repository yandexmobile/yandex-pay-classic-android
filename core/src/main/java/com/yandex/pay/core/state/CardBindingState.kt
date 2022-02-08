package com.yandex.pay.core.state

import android.net.Uri
import com.yandex.pay.core.infra.State
import com.yandex.xplat.yandex.pay.NewCard

internal data class CardBindingState(
    val cardDetails: NewCard?,
    val show3DS: Uri?,
) : State {
    internal companion object {
        fun create(): CardBindingState = CardBindingState(null, null)
    }
}
