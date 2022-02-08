package com.yandex.pay.core.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.yandex.pay.core.R
import com.yandex.pay.core.databinding.YandexpayCheckoutButtonBinding
import com.yandex.pay.core.ui.views.interfaces.ICheckoutButtonView
import com.yandex.pay.core.utils.hide
import com.yandex.pay.core.utils.show

internal class CheckoutButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes), ICheckoutButtonView {
    private val binding =
        YandexpayCheckoutButtonBinding.inflate(LayoutInflater.from(context), this)

    override var disabled: Boolean = true

    override var value: String = ""

    override var onClick: () -> Unit = {}

    override var state: ICheckoutButtonView.State = ICheckoutButtonView.State.NORMAL

    init {
        binding.yandexpayProgressBar.indeterminateDrawable.setColorFilter(
            ResourcesCompat.getColor(
                resources,
                R.color.yandexpay_checkout_progress_indication,
                context.theme
            ),
            android.graphics.PorterDuff.Mode.SRC_IN,
        )

        val background =
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.yandexpay_checkout_button_background,
                context.theme
            )
        with(binding.root) {
            this.background = background
            foreground =
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.yandexpay_checkout_button_ripple,
                    context.theme
                )
            isClickable = true
            isFocusable = true
        }

        binding.yandexpayShimmer.background = background
        setOnClickListener {
            onClick()
        }
        update()
    }

    override fun update() {
        when (state) {
            ICheckoutButtonView.State.LOADING -> switchToLoading()
            ICheckoutButtonView.State.CHECKING_OUT -> switchToCheckingOut()
            ICheckoutButtonView.State.CHECKED_OUT -> switchToCheckedOut()
            ICheckoutButtonView.State.NORMAL -> switchToNormal()
            ICheckoutButtonView.State.ERROR -> switchToError()
        }

        binding.yandexpayCheckoutButtonPrice.text = value
    }

    private fun switchToLoading() {
        binding.yandexpayCheckoutProgressIndicationGroup.hide()
        binding.yandexpayButtonContainer.hide()
        binding.yandexpayShimmer.show()
        isEnabled = false
        binding.root.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.yandexpay_checkout_button_progress_background,
            context.theme,
        )
        binding.yandexpayProgressIndicationTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0, 0, 0, 0,
        )
    }

    private fun switchToCheckingOut() {
        binding.yandexpayShimmer.hide()
        binding.yandexpayButtonContainer.hide()
        binding.yandexpayCheckoutProgressIndicationGroup.show()
        isEnabled = false
        binding.root.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.yandexpay_checkout_button_progress_background,
            context.theme,
        )
        binding.yandexpayProgressIndicationTitle.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.yandexpay_checkout_progress_indication,
                context.theme
            )
        )
        binding.yandexpayProgressIndicationTitle.setText(R.string.yandexpay_checkout_in_progress_title)
        binding.yandexpayProgressIndicationTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0, 0, 0, 0,
        )
    }

    private fun switchToCheckedOut() {
        binding.yandexpayShimmer.hide()
        binding.yandexpayButtonContainer.hide()
        binding.yandexpayCheckoutProgressIndicationGroup.show()
        binding.yandexpayProgressBar.hide()
        isEnabled = false
        binding.root.background =
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.yandexpay_checkout_button_done_background,
                context.theme
            )
        binding.yandexpayProgressIndicationTitle.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.yandexpay_checkout_progress_indication,
                context.theme
            )
        )
        binding.yandexpayProgressIndicationTitle.setText(R.string.yandexpay_checkout_done_title)
        binding.yandexpayProgressIndicationTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0, 0, R.drawable.yandexpay_ic_done_checkmark, 0,
        )
    }

    private fun switchToNormal() {
        binding.yandexpayShimmer.hide()
        binding.yandexpayCheckoutProgressIndicationGroup.hide()
        binding.yandexpayButtonContainer.show()
        isEnabled = !disabled
        val backgroundDrawable = if (disabled)
            R.drawable.yandexpay_checkout_button_disabled_background
        else
            R.drawable.yandexpay_checkout_button_background
        binding.root.background = ResourcesCompat.getDrawable(
            resources,
            backgroundDrawable,
            context.theme
        )
    }

    private fun switchToError() {
        binding.yandexpayShimmer.hide()
        binding.yandexpayButtonContainer.hide()
        binding.yandexpayCheckoutProgressIndicationGroup.hide()
        binding.yandexpayProgressIndicationTitle.show()
        isEnabled = false
        binding.root.background = ResourcesCompat.getDrawable(
            resources,
            R.drawable.yandexpay_checkout_button_error_background,
            context.theme,
        )
        binding.yandexpayProgressIndicationTitle.setTextColor(
            ResourcesCompat.getColor(
                resources,
                R.color.yandexpay_error,
                context.theme
            )
        )
        binding.yandexpayProgressIndicationTitle.setText(R.string.yandexpay_checkout_error_title)
        binding.yandexpayProgressIndicationTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
            0, 0, R.drawable.yandexpay_ic_error_mark, 0,
        )
    }
}
