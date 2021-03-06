// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM analytics/yandex-pay-analytics.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class GenericEventNames {
    companion object {
        @JvmStatic val INFO: String = "generic_info"
        @JvmStatic val WARN: String = "generic_warn"
        @JvmStatic val ERROR: String = "generic_error"
    }
}

public open class SpecificEventNames {
    companion object {
        @JvmStatic val PAY_CHECKOUT: String = "pay_checkout"
        @JvmStatic val SET_DEFAULT_CARD: String = "set_default_card"
        @JvmStatic val VALIDATE: String = "validate"
        @JvmStatic val USER_CARDS: String = "user_cards"
        @JvmStatic val IS_READY_TO_PAY: String = "is_ready_to_pay"
        @JvmStatic val LOAD_USER_PROFILE: String = "load_user_profile"
        @JvmStatic val SYNC_USER_CARD: String = "sync_user_profile"
        @JvmStatic val ENCRYPTED_APP_ID: String = "encrypted_app_id"
        @JvmStatic val ENCRYPTED_CARD: String = "encrypted_card"
        @JvmStatic val BANK_LOGOS: String = "bank_logos"
        @JvmStatic val BIND_NEW_CARD: String = "bind_new_card"
        @JvmStatic val BIND_NEW_CARD_POLLING: String = "bind_new_card_polling"
        @JvmStatic val BIND_NEW_CARD_VERIFY_COMPLETE: String = "bind_new_card_verify_complete"
        @JvmStatic val BIND_NEW_CARD_VERIFY_START: String = "bind_new_card_verify_start"
        @JvmStatic val BIND_NEW_CARD_BINDING_COMPLETE: String = "bind_new_card_binding_complete"
        @JvmStatic val BIND_NEW_CARD_BINDING_STARTED: String = "bind_new_card_binding_started"
        @JvmStatic val GET_ALLOWED_BINS: String = "get_allowed_bins"
        @JvmStatic val REGISTER_PUSH_TOKEN: String = "register_push_token"
        @JvmStatic val IS_AUTHORIZED: String = "is_authorized"
        @JvmStatic val INIT_INSTALL_REWARD: String = "init_install_reward"
        @JvmStatic val GET_INSTALL_REWARD: String = "get_install_reward"
    }
}

public open class EventParams {
    companion object {
        @JvmStatic val IS_DEBUG: String = "is_debug"
        @JvmStatic val MESSAGE: String = "message"
        @JvmStatic val PAY_VERSION: String = "pay_version"
        @JvmStatic val HOST_APP: String = "host_app"
    }
}

public open class YandexPayAnalyticsEvents {
    open fun bindNewCardPollingStatus(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.BIND_NEW_CARD_POLLING)
    }

    open fun bindNewCardVerifyCompleted(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.BIND_NEW_CARD_VERIFY_COMPLETE)
    }

    open fun bindNewCardVerify(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.BIND_NEW_CARD_VERIFY_START)
    }

    open fun bindNewCardBindingCompleted(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.BIND_NEW_CARD_BINDING_COMPLETE)
    }

    open fun bindNewCardBinding(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.BIND_NEW_CARD_BINDING_STARTED)
    }

    open fun bindNewCard(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.BIND_NEW_CARD)
    }

    open fun payCheckout(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.PAY_CHECKOUT)
    }

    open fun setDefaultCard(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.SET_DEFAULT_CARD)
    }

    open fun validate(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.VALIDATE)
    }

    open fun userCards(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.USER_CARDS)
    }

    open fun isReadyToPay(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.IS_READY_TO_PAY)
    }

    open fun loadUserProfile(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.LOAD_USER_PROFILE)
    }

    open fun syncUserCard(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.SYNC_USER_CARD)
    }

    open fun encryptedAppId(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.ENCRYPTED_APP_ID)
    }

    open fun encryptedCard(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.ENCRYPTED_CARD)
    }

    open fun bankLogos(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.BANK_LOGOS)
    }

    open fun getAllowedBins(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.GET_ALLOWED_BINS)
    }

    open fun registerPushToken(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.REGISTER_PUSH_TOKEN)
    }

    open fun isAuthorized(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.IS_AUTHORIZED)
    }

    open fun initInstallReward(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.INIT_INSTALL_REWARD)
    }

    open fun getInstallReward(): EventusEvent {
        return YandexPayAnalytics.buildEvent(SpecificEventNames.GET_INSTALL_REWARD)
    }

}

public open class YandexPayAnalyticsEnvironment {
    private val additionalParams: YSMap<String, Any> = mutableMapOf()
    open fun getAdditionalParams(): YSMap<String, Any> {
        return this.additionalParams
    }

    open fun reset(): YandexPayAnalyticsEnvironment {
        this.additionalParams.clear()
        return this
    }

}

public open class YandexPayAnalytics {
    companion object {
        @JvmStatic val environment: YandexPayAnalyticsEnvironment = YandexPayAnalyticsEnvironment()
        @JvmStatic val events: YandexPayAnalyticsEvents = YandexPayAnalyticsEvents()
        @JvmStatic
        open fun buildEvent(eventName: String, params: MapJSONItem = MapJSONItem()): EventusEvent {
            return EventusEvent.newClientEvent(eventName, ValueMapBuilder.__parse(params.asMap()))
        }

    }
}

