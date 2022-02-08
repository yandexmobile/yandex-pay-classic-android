package com.yandex.pay.core.ui.views

import android.content.Context
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import com.yandex.pay.core.R
import com.yandex.pay.core.databinding.YandexpayCvnInputBinding
import com.yandex.pay.core.ui.views.interfaces.ICvnInput
import com.yandex.pay.core.utils.Keyboard
import com.yandex.xplat.yandex.pay.CardValidationError

internal class CvnInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), ICvnInput {
    private val binding = YandexpayCvnInputBinding.inflate(LayoutInflater.from(context), this)

    var onKeyboardAction: () -> Unit = {}

    override var disabled: Boolean
        get() = !binding.yandexpayCvnInputText.isEnabled
        set(value) {
            binding.yandexpayCvnInputText.isEnabled = !value
        }

    override var textFieldValue: String
        get() = binding.yandexpayCvnInputText.text?.filter(Character::isDigit)
            ?.toString()
            .orEmpty()
        set(value) {
            binding.yandexpayCvnInputText.setText(value)
        }

    override var onFinishEditing: (() -> Unit)? = null
    override var onValueChanged: (() -> Unit)? = null
    override var onError: ((String?) -> Unit)? = null

    init {
        orientation = VERTICAL
        gravity = Gravity.START or Gravity.CENTER_VERTICAL
        binding.yandexpayCvnInputText.doAfterTextChanged {
            hideError()
            onValueChanged?.invoke()
        }
        binding.yandexpayCvnInputText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                onFinishEditing?.invoke()
            }
        }
        binding.yandexpayCvnInputText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                onKeyboardAction()
                true
            } else {
                false
            }
        }
    }

    override val hasFocus: Boolean
        get() = binding.yandexpayCvnInputText.hasFocus()

    fun gainFocus() {
        with(binding.yandexpayCvnInputText) {
            requestFocus()
            Keyboard.show(this)
        }
    }

    fun looseFocus() {
        binding.yandexpayCvnInputText.clearFocus()
    }

    override fun updateLengthFilter(length: Int) {
        binding.yandexpayCvnInputText.filters = arrayOf(LengthFilter(length))
    }

    override fun showError(error: CardValidationError): Boolean {
        hideError()
        return textFieldValue.isNotBlank().also {
            binding.yandexpayCvnInputLabel.setTextColor(
                ResourcesCompat.getColor(
                    resources,
                    R.color.yandexpay_card_binding_error_title,
                    context.theme,
                )
            )
            onError?.invoke(
                error.customErrorMessage
                    ?: resources.getString(R.string.yandexpay_wrong_cvn_message)
            )
        }
    }

    override fun hideError() {
        binding.yandexpayCvnInputLabel.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.yandexpay_card_edit_hint,
                context.theme,
            )
        )
        onError?.invoke(null)
    }
}
