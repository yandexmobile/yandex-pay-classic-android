package com.yandex.pay.core.ui.views.presenters

import com.yandex.pay.core.ui.views.interfaces.Controller
import com.yandex.pay.core.ui.views.interfaces.ICvnInput
import com.yandex.xplat.yandex.pay.*

internal class CvnInputController(
    private val validator: CardFieldValidator<CardCvnField>,
): Controller<CvnInputController.Payload, ICvnInput> {
    class Payload(val onDone: (String) -> Unit)

    private var view: ICvnInput? = null
    private val requireView: ICvnInput
        get() = requireNotNull(view)

    private var onDone: ((String) -> Unit)? = null

    override fun bind(data: Payload, view: ICvnInput) {
        if (this.view != null && this.view != view) {
            throw IllegalStateException("Controller is already bound to another view. Unbind first.")
        }
        this.view = view
        this.onDone = data.onDone
        requireView.onFinishEditing = ::onFinishEditing
        requireView.updateLengthFilter(cardType.cvvLength)
    }

    override fun unbind(view: ICvnInput) {
        if (this.view != null && this.view != view) {
            throw IllegalStateException("Controller is actually bound to another view.")
        }
        onDone = null
        this.view?.apply {
            onFinishEditing = null
        }
        this.view = null
    }

    private fun onFinishEditing() {
        val error = validate()
        if (error != null) {
            requireView.showError(error)
        } else {
            requireView.hideError()
            onDone!!(requireView.textFieldValue)
        }
    }

    var cardType: CardType = CardType.UNKNOWN
        set(value) {
            field = value
            requireView.updateLengthFilter(field.cvvLength)
        }

    fun validate(): CardValidationError? = validator.composite()
        .addValidator(LengthCardCvnValidator.withCardNetwork(cardType.cardNetwork))
        .validate(CardField.cvn(requireView.textFieldValue))

    val isValid: Boolean
        get() = validate() == null

    val hasCvn: Boolean
        get() = isValid && requireView.textFieldValue.isNotBlank()

    val hasFocus: Boolean
        get() = requireView.hasFocus
}
