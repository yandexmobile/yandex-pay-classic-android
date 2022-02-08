package com.yandex.pay.core.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import com.yandex.pay.core.R
import com.yandex.pay.core.databinding.YandexpayCardItemBinding
import com.yandex.pay.core.ui.views.interfaces.ICardItemView
import com.yandex.pay.core.utils.hide
import com.yandex.pay.core.utils.show

internal class CardItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes), ICardItemView {
    private val binding: YandexpayCardItemBinding = YandexpayCardItemBinding.inflate(
        LayoutInflater.from(context),
        this,
    )

    init {
        val background =
            ResourcesCompat.getDrawable(resources, R.drawable.yandexpay_card_border, context.theme)
        with(binding.root) {
            foreground = ResourcesCompat.getDrawable(
                resources,
                R.drawable.yandexpay_card_ripple,
                context.theme
            )
            isClickable = true
            isFocusable = true
            this.background = background
        }
        binding.yandexpayShimmer.background = background
    }

    override var accessoryType: ICardItemView.AccessoryType = ICardItemView.AccessoryType.None

    override var title: String = ""

    override var subtitle: String = ""

    @DrawableRes
    override var image: Int? = null

    override var onClick: () -> Unit = {}
        set(value) {
            field = value
            setOnClickListener {
                value()
            }
        }

    override var loading: Boolean = false

    init {
        update()
    }

    override fun update() {
        if (loading) {
            binding.yandexpayCardTitle.hide()
            binding.yandexpayCardSubtitle.hide()
            binding.yandexpayCardItemImage.hide()
            binding.yandexpayCardItemAccessoryImageCenter.hide()
            binding.yandexpayCardItemAccessoryImageTop.hide()
            binding.yandexpayShimmer.show()
            return
        }

        binding.yandexpayShimmer.hide()
        binding.yandexpayCardTitle.show()
        binding.yandexpayCardItemImage.show()
        when (accessoryType) {
            ICardItemView.AccessoryType.None -> {
                binding.yandexpayCardItemAccessoryImageTop.hide()
                binding.yandexpayCardItemAccessoryImageCenter.hide()
            }
            ICardItemView.AccessoryType.Disclosure -> {
                binding.yandexpayCardItemAccessoryImageTop.hide()
                binding.yandexpayCardItemAccessoryImageCenter.show()
                setupAccessoryImage(
                    binding.yandexpayCardItemAccessoryImageCenter,
                    R.drawable.yandexpay_ic_arrow
                )
            }
            ICardItemView.AccessoryType.Checkmark -> {
                binding.yandexpayCardItemAccessoryImageCenter.hide()
                binding.yandexpayCardItemAccessoryImageTop.show()
                setupAccessoryImage(
                    binding.yandexpayCardItemAccessoryImageTop,
                    R.drawable.yandexpay_ic_checkmark
                )
            }
        }

        binding.yandexpayCardTitle.text = title
        if (subtitle.isEmpty()) {
            binding.yandexpayCardSubtitle.hide()
            binding.yandexpayCardTitle.maxLines = 2
        } else {
            binding.yandexpayCardSubtitle.show()
            binding.yandexpayCardSubtitle.text = subtitle
            binding.yandexpayCardTitle.maxLines = 1
        }
        binding.yandexpayCardItemImage.setImageDrawable(image?.let {
            ResourcesCompat.getDrawable(
                resources,
                it,
                context.theme
            )
        })
    }

    private fun setupAccessoryImage(imageView: ImageView, @DrawableRes image: Int) {
        imageView.setImageDrawable(
            ResourcesCompat.getDrawable(
                resources,
                image,
                context.theme
            )
        )
    }
}
