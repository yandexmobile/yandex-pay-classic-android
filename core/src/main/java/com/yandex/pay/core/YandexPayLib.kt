package com.yandex.pay.core

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import com.yandex.pay.core.data.LastUsedCard
import com.yandex.pay.core.data.Merchant
import com.yandex.pay.core.data.PaymentCheckoutResult
import com.yandex.pay.core.data.PaymentMethod
import com.yandex.pay.core.di.ComponentsHolder
import com.yandex.pay.core.events.EventusMetrica
import com.yandex.pay.core.events.MetricaLogger
import com.yandex.pay.core.storage.*
import com.yandex.pay.core.userprofile.AvatarLoader
import com.yandex.pay.core.utils.MainThreadRunner
import com.yandex.pay.core.utils.YandexPayLibException
import com.yandex.xplat.common.NetworkConfig
import com.yandex.xplat.common.createSSLContentCreator
import java.util.concurrent.Executors

/**
 * The Y.Pay library entry point. Use it to setup Y.Pay and run requests.
 */
class YandexPayLib private constructor() {
    internal lateinit var componentsHolder: ComponentsHolder

    /**
     * Processes the return value of the Y.Pay Activity.
     * It extracts the payment token and details, or error from the resulting intent and returns [PaymentCheckoutResult]
     * @param context Activity Context that got [android.app.Activity.onActivityResult].
     * @param resultCode the result code of the [android.app.Activity.startActivityForResult] run, passed to [android.app.Activity.onActivityResult].
     * @param data the returning data that was passed into [android.app.Activity.onActivityResult].
     * @return the result with Payment Checkout result if the run is successful, or null if unknown error occurred.
     */
    fun processActivityResult(
        context: Context,
        resultCode: Int,
        data: Intent?
    ): kotlin.Result<PaymentCheckoutResult>? =
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (PaymentCheckoutResult.isErrorResult(data)) {
                kotlin.Result.failure(
                    YandexPayLibException.from(
                        PaymentCheckoutResult.extractError(
                            data
                        )!!, context.resources
                    )!!
                )
            } else {
                kotlin.Result.success(PaymentCheckoutResult.from(data)!!)
            }
        } else {
            null
        }

    /**
     * Allows checking if the merchant is allowed to conduct a payment with Y.Pay.
     * If not, Y.Pay button may be hidden or removed.
     */
    fun isReadyToPay(
        merchant: Merchant,
        paymentMethods: List<PaymentMethod>,
        completion: Result<Boolean, YandexPayLibException>,
    ) {
        componentsHolder.payApi.isReadyToPay(merchant, true, paymentMethods) { result ->
            result
                .onSuccess(completion::onSuccess)
                .onFailure { completion.onFailure(YandexPayLibException.from(it)) }
        }
    }

    private val mutableCurrentCard: MutableLiveData<LastUsedCard> = MutableLiveData()
    internal val currentCard: LiveData<LastUsedCard> = mutableCurrentCard.distinctUntilChanged()

    internal val avatar: MutableLiveData<Drawable?> = MutableLiveData()

    companion object {
        @Volatile
        private var _instance: YandexPayLib? = null

        /**
         * The instance of Y.Pay library.
         */
        @JvmStatic
        val instance: YandexPayLib
            get() = checkNotNull(_instance) { "Yandex.Pay must be initialized before use." }

        /**
         * Checks if the library is supported on that system.
         */
        val isSupported: Boolean get() = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M

        /**
         * Initializes the library. Must be called before any operations with the library,
         * i.e. before displaying the Y.Pay Button.
         *
         * @param config configuration options of the library. See [YandexPayLibConfig] for details.
         * @param context Application context.
         */
        fun initialize(config: YandexPayLibConfig, context: Context) {
            if (!isSupported) {
                throw IllegalStateException("Yandex.Pay is unsupported on the platform. Use isSupported to check in advance.")
            }
            if (_instance == null) {
                synchronized(YandexPayLib::class) {
                    if (_instance == null) {
                        val logging = config.logging
                        val debug = false

                        MetricaLogger.setup(
                            context.applicationContext,
                            logging,
                            debug,
                        )
                        _instance = YandexPayLib().apply {
                            val sharedPreferences = getSharedPreferences(context)
                            val currentCardStorage = NotifyingCurrentCardStorage(
                                mutableCurrentCard,
                                SharedPreferencesCurrentCardStorage(sharedPreferences)
                            )
                            componentsHolder = ComponentsHolder(
                                config,
                                SharedPreferencesAuthTokenStorage(sharedPreferences),
                                SharedPreferencesUserProfileStorage(sharedPreferences),
                                AvatarLoader(context, Executors.newSingleThreadExecutor()),
                                MainThreadRunner(),
                                EventusMetrica(),
                                CurrentCardChanger(currentCardStorage),
                                currentCardStorage,
                                context.applicationContext.resources,
                                NetworkConfig(
                                    logging,
                                    createSSLContentCreator(debug),
                                    emptyList(),
                                    null,
                                    null,
                                ),
                            )
                            mutableCurrentCard.postValue(currentCardStorage.load())
                        }
                    }
                }
            }
        }

        private fun getSharedPreferences(context: Context): SharedPreferences =
            context.getSharedPreferences(
                "ypay_preferences",
                Context.MODE_PRIVATE
            )
    }
}
