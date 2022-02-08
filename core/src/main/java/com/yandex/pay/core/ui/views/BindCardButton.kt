package com.yandex.pay.core.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.yandex.pay.core.R
import com.yandex.pay.core.databinding.YandexpayBindCardButtonBinding
import com.yandex.pay.core.ui.views.interfaces.IBindCardButtonView
import com.yandex.pay.core.utils.hide
import com.yandex.pay.core.utils.show

internal class BindCardButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes), IBindCardButtonView {
    private val binding = YandexpayBindCardButtonBinding.inflate(LayoutInflater.from(context), this)

    override var state: IBindCardButtonView.State = IBindCardButtonView.State.NORMAL

    override var disabled: Boolean = false

    override var title: String = ""

    override var onClick: () -> Unit = { }

    init {
        isFocusable = true
        isClickable = true
        background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.yandexpay_bind_card_button_background,
            context.theme
        )
        foreground = ResourcesCompat.getDrawable(
            resources,
            R.drawable.yandexpay_bind_card_button_ripple,
            context.theme
        )
        binding.yandexpayProgressBar.indeterminateDrawable.setColorFilter(
            resources.getColor(R.color.yandexpay_card_binding_progress_indication, context.theme),
            android.graphics.PorterDuff.Mode.SRC_IN,
        )

        setOnClickListener {
            if (!disabled && state == IBindCardButtonView.State.NORMAL) onClick()
        }

        update()
    }

    override fun update() {
        when (state) {
            IBindCardButtonView.State.NORMAL -> switchToNormal()
            IBindCardButtonView.State.ERROR -> switchToError()
            IBindCardButtonView.State.DONE -> switchToDone()
            IBindCardButtonView.State.PROGRESS -> switchToProgress()
        }
    }

    private fun switchToProgress() {
        isFocusable = false
        isClickable = false
        background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.yandexpay_bind_card_button_progress_background,
            context.theme
        )

        binding.yandexpayBindCardButtonTitle.setText(R.string.yandexpay_bind_card_progress_title)
        binding.yandexpayBindCardButtonTitle.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.yandexpay_card_binding_progress_indication,
                context.theme
            )
        )
        binding.yandexpayBindCardButtonTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            0,
            0
        )

        binding.yandexpayProgressBar.show()
        binding.yandexpayBindCardButtonTitle.show()
    }

    private fun switchToDone() {
        binding.yandexpayProgressBar.hide()

        isFocusable = false
        isClickable = false
        background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.yandexpay_bind_card_button_done_background,
            context.theme
        )

        binding.yandexpayBindCardButtonTitle.setText(R.string.yandexpay_bind_card_done_title)
        binding.yandexpayBindCardButtonTitle.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.yandexpay_card_binding_done,
                context.theme
            )
        )
        binding.yandexpayBindCardButtonTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            R.drawable.yandexpay_ic_done_checkmark,
            0
        )

        binding.yandexpayBindCardButtonTitle.show()
    }

    private fun switchToNormal() {
        binding.yandexpayProgressBar.hide()

        isFocusable = !disabled
        isClickable = !disabled
        isEnabled = !disabled
        background = ResourcesCompat.getDrawable(
            resources,
            if (disabled) R.drawable.yandexpay_bind_card_button_disabled_background else R.drawable.yandexpay_bind_card_button_background,
            context.theme
        )

        binding.yandexpayBindCardButtonTitle.setTextColor(
            ResourcesCompat.getColor(
                resources,
                if (disabled) R.color.yandexpay_card_binding_disabled else R.color.yandexpay_card_binding_button_title,
                context.theme
            )
        )
        binding.yandexpayBindCardButtonTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            0,
            0
        )
        binding.yandexpayBindCardButtonTitle.text = title
        binding.yandexpayBindCardButtonTitle.show()
    }

    private fun switchToError() {
        binding.yandexpayProgressBar.hide()

        isFocusable = false
        isClickable = false
        isEnabled = true
        background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.yandexpay_bind_card_button_error_background,
            context.theme
        )

        binding.yandexpayBindCardButtonTitle.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.yandexpay_card_binding_error_title,
                context.theme
            )
        )
        binding.yandexpayBindCardButtonTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0,
            0,
            R.drawable.yandexpay_ic_error_mark,
            0
        )
        binding.yandexpayBindCardButtonTitle.text = title
        binding.yandexpayBindCardButtonTitle.show()
    }
}
