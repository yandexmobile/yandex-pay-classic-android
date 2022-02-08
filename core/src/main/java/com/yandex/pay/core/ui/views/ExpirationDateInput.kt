package com.yandex.pay.core.ui.views

import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import com.yandex.pay.core.R
import com.yandex.pay.core.databinding.YandexpayExpirationDateInputBinding
import com.yandex.pay.core.ui.views.interfaces.IExpirationDateInput
import com.yandex.pay.core.utils.Keyboard
import com.yandex.xplat.yandex.pay.CardValidationError

internal class ExpirationDateInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), IExpirationDateInput {
    private val binding = YandexpayExpirationDateInputBinding.inflate(
        LayoutInflater.from(context),
        this
    )

    override var onFinishEditing: (() -> Unit)? = null
    override var contentFormatter: ((Editable, Boolean) -> Unit)? = null
    override var onValueChanged: (() -> Unit)? = null
    override var onError: ((String?) -> Unit)? = null

    override var disabled: Boolean
        get() = !binding.yandexpayExpirationDateInputText.isEnabled
        set(value) {
            binding.yandexpayExpirationDateInputText.isEnabled = !value
        }

    var onKeyboardAction: () -> Unit = {}

    init {
        orientation = VERTICAL
        gravity = Gravity.START or Gravity.CENTER_VERTICAL
        binding.yandexpayExpirationDateInputText.addTextChangedListener(object :
            TextWatcher {
            private var mChangeWasAddition = false

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mChangeWasAddition = count > before
            }

            override fun afterTextChanged(editable: Editable) {
                contentFormatter?.invoke(editable, mChangeWasAddition)
                onValueChanged?.invoke()
            }
        })
        binding.yandexpayExpirationDateInputText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                onFinishEditing?.invoke()
            }
        }
        binding.yandexpayExpirationDateInputText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                onKeyboardAction()
                true
            } else {
                false
            }
        }
    }

    override var textFieldValue: String
        get() = binding.yandexpayExpirationDateInputText.text?.toString().orEmpty()
        set(value) {
            binding.yandexpayExpirationDateInputText.setText(value)
        }

    override fun showError(error: CardValidationError): Boolean {
        hideError()
        return textFieldValue.isNotBlank().also {
            binding.yandexpayExpirationDateInputLabel.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.yandexpay_card_binding_error_title,
                    context.theme,
                )
            )
            onError?.invoke(
                error.customErrorMessage
                    ?: resources.getString(R.string.yandexpay_wrong_expiration_date_message)
            )
        }
    }

    override fun hideError() {
        binding.yandexpayExpirationDateInputLabel.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.yandexpay_card_edit_hint,
                context.theme,
            )
        )
        onError?.invoke(null)
    }

    override val hasFocus: Boolean
        get() = binding.yandexpayExpirationDateInputText.hasFocus()

    fun gainFocus() {
        with(binding.yandexpayExpirationDateInputText) {
            requestFocus()
            Keyboard.show(this)
        }
    }

    fun looseFocus() {
        binding.yandexpayExpirationDateInputText.clearFocus()
    }

    override fun resetInputFilters(maxChars: Int) {
        binding.yandexpayExpirationDateInputText.filters =
            arrayOf(
                DigitsKeyListener(),
                InputFilter.LengthFilter(maxChars)
            )
    }
}

