package com.yandex.pay.core.ui.views.interfaces

import android.text.Editable
import com.yandex.xplat.yandex.pay.CardValidationError

internal interface IExpirationDateInput : View {
    var contentFormatter: ((Editable, Boolean) -> Unit)?
    var onFinishEditing: (() -> Unit)?
    var onValueChanged: (() -> Unit)?
    var onError: ((String?) -> Unit)?

    var textFieldValue: String
    val hasFocus: Boolean
    var disabled: Boolean

    fun showError(error: CardValidationError): Boolean
    fun hideError()
    fun resetInputFilters(maxChars: Int)
}
