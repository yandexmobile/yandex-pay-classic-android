package com.yandex.pay.core.ui.views.presenters

import android.text.Editable
import android.text.Spanned
import com.yandex.pay.core.data.CardDetails
import com.yandex.pay.core.ui.views.SlashSpan
import com.yandex.pay.core.ui.views.interfaces.Controller
import com.yandex.pay.core.ui.views.interfaces.IExpirationDateInput
import com.yandex.xplat.yandex.pay.CardExpirationDateField
import com.yandex.xplat.yandex.pay.CardField
import com.yandex.xplat.yandex.pay.CardFieldValidator
import com.yandex.xplat.yandex.pay.CardValidationError

internal class ExpirationDateInputController(
    private val validator: CardFieldValidator<CardExpirationDateField>,
) : Controller<ExpirationDateInputController.Payload, IExpirationDateInput> {
    internal class Date(val month: Int, val year: Int) {
        internal companion object {
            fun from(cardDetails: CardDetails): Date? {
                val (_, month, year, _) = cardDetails
                return if (month != null && year != null) Date(month, year) else null
            }
        }
    }

    class Payload(val data: Date?, val onDone: (Date) -> Unit)

    private var view: IExpirationDateInput? = null
    private val requireView: IExpirationDateInput
        get() = requireNotNull(view)

    private var onDone: ((Date) -> Unit)? = null

    override fun bind(data: Payload, view: IExpirationDateInput) {
        if (this.view != null && this.view != view) {
            throw IllegalStateException("Controller is already bound to another view. Unbind first.")
        }
        this.view = view
        this.onDone = data.onDone
        requireView.contentFormatter = ::formatContent
        requireView.onFinishEditing = ::onFinishEditing
        requireView.resetInputFilters(MAX_NUM_CHARS)
    }

    override fun unbind(view: IExpirationDateInput) {
        if (this.view != null && this.view != view) {
            throw IllegalStateException("Controller is actually bound to another view.")
        }
        onDone = null
        this.view?.apply {
            contentFormatter = null
            onFinishEditing = null
            onValueChanged = null
        }
        this.view = null
    }

    val hasExpirationDate: Boolean
        get() = isValid && expirationMonth.isNotBlank() && expirationYear.isNotBlank()

    val isValid: Boolean
        get() = validate() == null

    val hasFocus: Boolean
        get() = requireView.hasFocus

    fun validate(): CardValidationError? =
        validator.validate(CardField.expirationDate(expirationMonth, expirationYear))

    private val expirationMonth: String
        get() = requireView.textFieldValue.takeIf { it.length >= MONTH_LENGTH }
            ?.substring(0, MONTH_LENGTH)
            .orEmpty()

    private val expirationYear: String
        get() = requireView.textFieldValue.takeIf { it.length >= MONTH_LENGTH + YEAR_LENGTH }
            ?.substring(MONTH_LENGTH).orEmpty()

    private fun onFinishEditing() {
        val error = validate()
        if (error != null) {
            requireView.showError(error)
        } else {
            requireView.hideError()
            onDone!!(Date(expirationMonth.toInt(), expirationYear.toInt()))
        }
    }

    private fun formatContent(value: Editable, changeWasAddition: Boolean) {
        if (changeWasAddition) {
            if (value.length == 1 && Character.getNumericValue(value[0]) > 1) {
                prependLeadingZero(value)
            }
        }

        val paddingSpans = value.getSpans(
            0, value.length, SlashSpan::class.java
        )
        paddingSpans.forEach(value::removeSpan)
        addDateSlash(value)
        requireView.hideError()
    }

    private fun prependLeadingZero(editable: Editable) {
        val firstChar = editable[0]
        editable.replace(0, 1, "0").append(firstChar)
    }

    private fun addDateSlash(editable: Editable) {
        val index = MONTH_LENGTH
        val length = editable.length
        if (index <= length) {
            editable.setSpan(
                SlashSpan(), index - 1, index,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private companion object {
        const val MONTH_LENGTH: Int = 2
        const val YEAR_LENGTH: Int = 2
        const val MAX_NUM_CHARS: Int = MONTH_LENGTH + YEAR_LENGTH
    }
}
