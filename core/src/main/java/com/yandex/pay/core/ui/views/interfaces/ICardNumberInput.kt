package com.yandex.pay.core.ui.views.interfaces

import androidx.annotation.DrawableRes
import com.yandex.xplat.yandex.pay.CardValidationError

internal interface ICardNumberInput : View {
    enum class TextState {
        FULL, MASKED
    }

    var contentFormatter: ((String) -> String?)?
    var onFinishEditing: (() -> Unit)?
    var onFocusGained: (() -> Unit)?
    var onValueChanged: (() -> Unit)?
    var onError: ((String?) -> Unit)?

    var textFieldValue: String
    val hasFocus: Boolean
    var disabled: Boolean

    fun updateCardTypeView(@DrawableRes image: Int)
    fun moveCursor()

    fun showError(error: CardValidationError): Boolean
    fun hideError()
}
