package com.yandex.pay.core.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Observer
import com.yandex.pay.core.IntentBuilder
import com.yandex.pay.core.R
import com.yandex.pay.core.YandexPayLib
import com.yandex.pay.core.data.LastUsedCard
import com.yandex.pay.core.data.UserCard
import com.yandex.pay.core.data.UserProfile
import com.yandex.pay.core.databinding.YandexpayPayButtonBinding
import com.yandex.pay.core.di.ComponentsHolder
import com.yandex.pay.core.events.Event
import com.yandex.pay.core.ui.views.presenters.AvatarPresenter
import com.yandex.pay.core.userprofile.UserProfileDataSource
import com.yandex.pay.core.userprofile.UserProfileLoader
import com.yandex.pay.core.utils.CardDetailsFormatter
import com.yandex.pay.core.utils.YandexPayLibException
import com.yandex.pay.core.utils.hide
import com.yandex.pay.core.utils.show

/**
 * The personalizable Y.Pay button.
 */
class YandexPayButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    fun interface OnClickListener {
        /**
         * Represents a reaction to the Button tap. The argument is required to be filled with [com.yandex.pay.core.data.OrderDetails]
         * @param intent An [IntentBuilder] that is setup to build an intent to show with [android.app.Activity.startActivityForResult].
         */
        fun onClick(intent: IntentBuilder)
    }

    /**
     * Defines a way the Button is personalized
     */
    enum class Personalization {
        /**
         * The Button always shows "Pay with YPay" text, never becomes personalized. Default value.
         */
        NONE,

        /**
         * The button is personalized: shows the last used card (if any) and user's name and avatar.
        The button also updates its content upon attaching to window.
         */
        UPDATING,

        /**
         * The button is personalized: shows the last used card (if any) and user's name and avatar.
        The button doesn't update its contents upon displaying. Rather, shows the last used card number and user's name and avatar.
         */
        LAST_VALUE
    }

    private val binding: YandexpayPayButtonBinding =
        YandexpayPayButtonBinding.inflate(LayoutInflater.from(context), this)

    private val componentsHolder: ComponentsHolder
        get() = YandexPayLib.instance.componentsHolder

    private val formatter: CardDetailsFormatter =
        CardDetailsFormatter(CardDetailsFormatter.Context.PAY_BUTTON)
    private var lastUsedCardObserver: Observer<LastUsedCard>? = null

    private var avatarObserver: Observer<Drawable?>? = null

    /**
     * Personalization allows changing button representation. See [Personalization] for details.
     */
    var personalization: Personalization = extractCustomAttributes(attrs)
        set(value) {
            if (field != value) {
                field = value
                initialize(true)
            }
        }

    init {
        setup()
        super.setOnClickListener {
            throw IllegalStateException("Set onClickListener with setOnClickListener(YandexPayButton.OnClickListener) before use")
        }
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        throw IllegalStateException("Use setOnClickListener(YandexPayButton.OnClickListener) override for setting the listener")
    }

    /**
     * Setup a onClick listener. See [YandexPayButton.OnClickListener] for details.
     */
    fun setOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener {
            componentsHolder.metrica.log(Event.PayButtonTapped)
            listener?.onClick(IntentBuilder.create(context))
        }
    }

    /**
     * Updates the Button presentation from the server.
     * Gets the new avatar or user name, as well as checks the last used card availability.
     */
    fun updateFromNetwork(animated: Boolean = true, completion: ((Result<Unit>) -> Unit)? = null) {
        if (personalization != Personalization.LAST_VALUE) {
            throw IllegalStateException("Update from network operation is only supported with personalization type ${Personalization.LAST_VALUE.name}")
        }
        loadAvatar()
        val oldSelectedCard = componentsHolder.currentCardChanger.value
        loadUserCards { result ->
            result
                .onSuccess {
                    val newSelectedCard = componentsHolder.currentCardChanger.value
                    showPersonalized(
                        newSelectedCard,
                        YandexPayLib.instance.avatar.value,
                        animated && (newSelectedCard != oldSelectedCard)
                    )
                    completion?.invoke(Result.success(Unit))
                }
                .onFailure {
                    completion?.invoke(
                        Result.failure(
                            YandexPayLibException(
                                "Unable to update data for Y.Pay Button from network",
                                null
                            )
                        )
                    )
                }
        }
    }

    private fun extractCustomAttributes(attrs: AttributeSet?): Personalization {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.YandexPayButton, 0, 0)
        val brandingValue = try {
            attributes.getInt(R.styleable.YandexPayButton_yandexpay_personalization, 0)
        } finally {
            attributes.recycle()
        }
        return Personalization.values().getOrNull(brandingValue)
            ?: throw IllegalStateException("Unsupported personalization type: $brandingValue")
    }

    private fun setup() {
        foreground = ResourcesCompat.getDrawable(
            resources,
            R.drawable.yandexpay_pay_button_ripple,
            context.theme
        )
        background =
            ResourcesCompat.getDrawable(resources, R.drawable.yandexpay_pay_button, context.theme)
        binding.yandexpayAvatarImage.clipToOutline = true
    }

    private fun showGeneric(animated: Boolean) {
        toggle(binding.yandexpayPayButtonPersonalized, binding.yandexpayPayButtonGeneric, animated)
    }

    private fun showPersonalized(
        lastUsedCard: LastUsedCard?,
        avatar: Drawable?,
        animated: Boolean
    ) {
        if (lastUsedCard != null) {
            showAvatar(avatar)
            showLastUsedCard(lastUsedCard, animated)
        } else {
            showGeneric(animated)
        }
    }

    private fun showAvatar(avatar: Drawable?) {
        if (avatar != null && personalization != Personalization.NONE) {
            binding.yandexpayAvatarImage.setImageDrawable(avatar)
            binding.yandexpayAvatarImage.show()
        } else {
            binding.yandexpayAvatarImage.hide()
        }
    }

    private fun showLastUsedCard(value: LastUsedCard, animated: Boolean) {
        val number = formatter.getCardNumberTitle(resources, value.last4Digits)
        val image = formatter.getCardNetworkImage(value.network)
        toggle(
            binding.yandexpayPayButtonGeneric,
            binding.yandexpayPayButtonPersonalized,
            animated
        ) {
            binding.yandexpayAvatarImage.setImageDrawable(YandexPayLib.instance.avatar.value)
            binding.yandexpayCardSubtitle.text = number
            binding.yandexpayCardSubtitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                image, 0, 0, 0
            )
        }
    }

    private fun toggle(
        hiding: View,
        showing: View,
        animated: Boolean,
        setupBlock: (() -> Unit)? = null
    ) {
        if (animated) {
            val duration =
                resources.getInteger(R.integer.yandexpay_pay_button_toggle_duration).toLong()
            val interpolator = LinearInterpolator()
            hiding.animate()
                .alpha(0.0f)
                .setDuration(duration)
                .setInterpolator(interpolator)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        hiding.hide()
                        hiding.alpha = 1.0f

                        setupBlock?.invoke()

                        showing.alpha = 0.0f
                        showing.show()
                        showing.animate()
                            .alpha(1.0f)
                            .setDuration(duration)
                            .setInterpolator(interpolator)
                            .setListener(null)
                    }
                })
        } else {
            hiding.hide()
            setupBlock?.invoke()
            showing.show()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initialize(false)
    }

    override fun onDetachedFromWindow() {
        removeObserver()
        super.onDetachedFromWindow()
    }

    private fun removeObserver() {
        if (lastUsedCardObserver != null) {
            YandexPayLib.instance.currentCard.removeObserver(lastUsedCardObserver!!)
            lastUsedCardObserver = null
        }
        if (avatarObserver != null) {
            YandexPayLib.instance.avatar.removeObserver(avatarObserver!!)
            avatarObserver = null
        }
    }

    private var cardsLoaded: Boolean = false
    private fun initialize(animated: Boolean) {
        removeObserver()
        when (personalization) {
            Personalization.NONE -> showGeneric(animated)
            Personalization.LAST_VALUE, Personalization.UPDATING -> {
                var initialValueReceived = animated
                lastUsedCardObserver = Observer {
                    showPersonalized(
                        it,
                        YandexPayLib.instance.avatar.value,
                        initialValueReceived
                    )
                    initialValueReceived = true
                }
                YandexPayLib.instance.currentCard.observeForever(lastUsedCardObserver!!)
            }
        }
        avatarObserver = Observer(this::showAvatar)
        YandexPayLib.instance.avatar.observeForever(avatarObserver!!)
        initializeAvatar()
        if (personalization == Personalization.UPDATING && !cardsLoaded) {
            loadUserCards(null)
            loadAvatar()
        }
        cardsLoaded = true
    }

    private fun loadUserCards(completion: ((Result<List<UserCard>>) -> Unit)?) {
        componentsHolder.payApi.getUserCards { result ->
            result
                .onSuccess { list ->
                    componentsHolder.metrica.log(Event.PayButtonUpdate(null))
                    componentsHolder.currentCardChanger.change(list)
                }
                .onFailure {
                    componentsHolder.metrica.log(Event.PayButtonUpdate(it))
                }
            completion?.invoke(result)
        }
    }

    private fun loadAvatar() {
        val userProfileDataSource = UserProfileDataSource(
            UserProfileLoader(context, componentsHolder),
        )

        userProfileDataSource.fetch { payload ->
            when (payload) {
                AvatarPresenter.Payload.Loading -> Unit
                is AvatarPresenter.Payload.Data -> YandexPayLib.instance.avatar.postValue(
                    payload.profile.imageOrNull
                )
            }
        }
    }

    private fun initializeAvatar() {
        val userProfileLoader = UserProfileLoader(context, componentsHolder)
        val data = userProfileLoader.loadFromStorage()
        if (data != null && data is UserProfile.Resolved) {
            YandexPayLib.instance.avatar.value = data.imageOrNull
        }
    }
}
