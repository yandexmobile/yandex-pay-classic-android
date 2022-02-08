package com.yandex.pay.core.ui.views.interfaces

import com.yandex.xplat.yandex.pay.CardValidationError

internal interface ICvnInput : View {
    var onFinishEditing: (() -> Unit)?
    var onValueChanged: (() -> Unit)?
    var onError: ((String?) -> Unit)?

    var textFieldValue: String
    val hasFocus: Boolean
    var disabled: Boolean

    fun updateLengthFilter(length: Int)

    fun showError(error: CardValidationError): Boolean
    fun hideError()
}
