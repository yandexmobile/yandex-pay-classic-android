package com.yandex.pay.core.ui.views.presenters

import android.content.res.Resources
import com.yandex.pay.core.data.CardNetwork
import com.yandex.pay.core.ui.views.interfaces.Controller
import com.yandex.pay.core.ui.views.interfaces.ICardNumberInput
import com.yandex.pay.core.utils.CardDetailsFormatter
import com.yandex.xplat.yandex.pay.*

internal class CardNumberInputController(
    private val validator: CardFieldValidator<CardNumberField>,
    private val cardDetailsFormatter: CardDetailsFormatter,
    private val resources: Resources,
) : Controller<CardNumberInputController.Payload, ICardNumberInput> {
    internal sealed class Payload(val onDone: (String) -> Unit) {
        class Restore(val data: String, onDone: (String) -> Unit) : Payload(onDone)
        class Pristine(onDone: (String) -> Unit) : Payload(onDone)
    }

    private var current: String = ""
    private var view: ICardNumberInput? = null
    private val requireView: ICardNumberInput
        get() = requireNotNull(view)

    private var currentUnmaskedText: String? = null
    private var onDone: ((String) -> Unit)? = null

    val isValid: Boolean
        get() = validate() == null

    var state: ICardNumberInput.TextState = ICardNumberInput.TextState.FULL
        set(value) {
            if (value != field) {
                field = value
                updateTextState(field, requireView.textFieldValue)
            }
        }

    private var cardType: CardType? = null
        set(value) {
            if (field != value) {
                field = value
                redrawCardType(value)
            }
        }

    private fun redrawCardType(cardType: CardType?) {
        val image = cardType?.cardNetwork?.let(CardNetwork::from)
            ?.let(cardDetailsFormatter::getCardNetworkImage) ?: 0
        requireView.updateCardTypeView(image)
    }

    override fun bind(data: Payload, view: ICardNumberInput) {
        if (this.view != null && this.view != view) {
            throw IllegalStateException("Controller is already bound to another view. Unbind first.")
        }
        current = ""
        currentUnmaskedText = ""
        this.view = view
        this.onDone = data.onDone
        requireView.contentFormatter = ::formatTextFieldContents
        requireView.onFinishEditing = ::onFinishEditing

        when (data) {
            is Payload.Pristine -> Unit
            is Payload.Restore -> restoreState(data.data)
        }
    }

    override fun unbind(view: ICardNumberInput) {
        if (this.view != null && this.view != view) {
            throw IllegalStateException("Controller is actually bound to another view.")
        }
        current = ""
        currentUnmaskedText = ""
        onDone = null
        this.view?.apply {
            contentFormatter = null
            onFinishEditing = null
        }
        this.view = null
    }

    private fun restoreState(cardNumber: String) {
        cardType = null

        val filteredInput = cardNumber.filter(Char::isDigit)
        cardType = CardType.cardTypeFromCardNumber(filteredInput)
        current = cardDetailsFormatter.formatTextFieldContents(filteredInput).orEmpty()
        updateTextState(state, cardNumber)
    }

    private fun formatTextFieldContents(input: String): String? {
        if (input == current) {
            return null
        }
        when (state) {
            ICardNumberInput.TextState.FULL -> Unit
            ICardNumberInput.TextState.MASKED -> {
                // Do not format when in masked state
                return current
            }
        }

        val filteredInput = input.filter(Char::isDigit)
        updateCardType(CardType.cardTypeFromCardNumber(filteredInput))
        return cardDetailsFormatter.formatTextFieldContents(filteredInput)?.also { current = it }
            ?: current
    }

    fun validate(): CardValidationError? {
        val network = cardType?.cardNetwork ?: return CardValidationError.default
        return validator
            .composite()
            .addValidator(LengthCardNumberValidator.withCardNetwork(network))
            .validate(CardField.number(requireView.textFieldValue))
    }

    private fun updateCardType(type: CardType) {
        if (cardType?.cardNetwork != type.cardNetwork) {
            cardType = type
        }
    }

    private fun updateTextState(textState: ICardNumberInput.TextState, cardNumber: String) {
        requireView.textFieldValue = buildText(cardNumber)
        if (textState == ICardNumberInput.TextState.FULL) {
            requireView.moveCursor()
        }
    }

    private fun buildText(current: String): String {
        return when {
            state == ICardNumberInput.TextState.MASKED && !requireView.hasFocus -> {
                currentUnmaskedText = current
                cardDetailsFormatter.getCardNumberTitle(resources, current)
                    .also { this.current = it }
            }
            else -> {
                val value = currentUnmaskedText ?: current
                cardDetailsFormatter.applyFormatting(value, CardType.cardTypeFromCardNumber(value))
                    ?: value
            }
        }
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
}
