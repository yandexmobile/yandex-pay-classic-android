package com.yandex.pay.core.ui.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import com.yandex.pay.core.databinding.YandexpayAvatarViewBinding
import com.yandex.pay.core.ui.views.interfaces.IAvatarView
import com.yandex.pay.core.utils.hide
import com.yandex.pay.core.utils.show

internal class AvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), IAvatarView {
    private val binding: YandexpayAvatarViewBinding = YandexpayAvatarViewBinding.inflate(
        LayoutInflater.from(context), this)

    override var name: String = ""
    override var image: Drawable? = null
    override var onClick: () -> Unit = {}
    override var disabled: Boolean = false

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL or Gravity.END
        binding.yandexpayAvatarImage.clipToOutline = true

        update()
    }

    override fun update() {
        binding.yandexpayAvatarName.text = name
        if (image == null) {
            binding.yandexpayAvatarName.hide()
            binding.yandexpayAvatarImage.hide()
            binding.yandexpayShimmer.show()
        } else {
            binding.yandexpayShimmer.hide()
            binding.yandexpayAvatarName.show()
            binding.yandexpayAvatarImage.show()
            binding.yandexpayAvatarImage.setImageDrawable(image)
        }

        binding.root.isClickable = !disabled
        binding.root.isFocusable = !disabled
        binding.root.isEnabled = !disabled
        binding.root.setOnClickListener {
            if (!disabled) onClick()
        }
    }
}
