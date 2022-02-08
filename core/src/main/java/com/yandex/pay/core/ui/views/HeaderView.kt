package com.yandex.pay.core.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.yandex.pay.core.databinding.YandexpayViewHeaderBinding
import com.yandex.pay.core.ui.views.interfaces.IAvatarView
import com.yandex.pay.core.ui.views.interfaces.IHeaderView
import com.yandex.pay.core.utils.hide
import com.yandex.pay.core.utils.show

internal class HeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes), IHeaderView {
    private val binding = YandexpayViewHeaderBinding.inflate(LayoutInflater.from(context), this)

    override var root: Boolean = true
    override val avatar: IAvatarView = binding.yandexpayAvatar
    override var onBackButtonClick: (() -> Unit)? = null

    init {
        binding.yandexpayBackButton.setOnClickListener {
            backButtonTapped()
        }
        update()
    }

    override fun update() {
        if (root) {
            binding.yandexpayBackButton.hide()
            binding.yandexpayMainLogoTitle.show()
            binding.yandexpayAvatar.show()
        } else {
            binding.yandexpayMainLogoTitle.hide()
            binding.yandexpayAvatar.hide()
            binding.yandexpayBackButton.show()
        }
    }

    private fun backButtonTapped() {
        onBackButtonClick?.invoke()
    }
}
