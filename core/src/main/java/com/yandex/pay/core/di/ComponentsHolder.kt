package com.yandex.pay.core.di

import android.content.res.Resources
import android.util.Log
import com.yandex.pay.core.XPlatApi
import com.yandex.pay.core.YandexPayLibConfig
import com.yandex.pay.core.cardbinding.DiehardApi
import com.yandex.pay.core.data.OAuthToken
import com.yandex.pay.core.data.Uid
import com.yandex.pay.core.events.YPayMetrica
import com.yandex.pay.core.infra.Middleware
import com.yandex.pay.core.infra.Reducer
import com.yandex.pay.core.infra.Store
import com.yandex.pay.core.middleware.*
import com.yandex.pay.core.reducers.*
import com.yandex.pay.core.state.AppState
import com.yandex.pay.core.storage.AuthTokenStorage
import com.yandex.pay.core.storage.CurrentCardChanger
import com.yandex.pay.core.storage.CurrentCardStorage
import com.yandex.pay.core.storage.UserProfileStorage
import com.yandex.pay.core.userprofile.AvatarLoader
import com.yandex.pay.core.utils.Runner
import com.yandex.xplat.common.NetworkConfig

// Container for globally accessible items.
// We do not use Dagger, so that's a very loose emulation
internal class ComponentsHolder(
    val config: YandexPayLibConfig,
    val authTokenStorage: AuthTokenStorage,
    val userProfileStorage: UserProfileStorage,
    val avatarLoader: AvatarLoader,
    val mainThreadRunner: Runner,
    val metrica: YPayMetrica,
    val currentCardChanger: CurrentCardChanger,
    val currentCardStorage: CurrentCardStorage,
    val resources: Resources,
    private val networkConfig: NetworkConfig,
) {
    private var _payApi: XPlatApi? = null
    var payApi: XPlatApi
        get() {
            if (_payApi == null) {
                _payApi = XPlatApi(
                    authTokenStorage.load() ?: OAuthToken.empty(),
                    networkConfig,
                    testing = false,
                    mainThreadRunner
                )
            }
            return _payApi!!
        }
        private set(value) {
            _payApi = value
        }

    private var _diehardApi: DiehardApi? = null
    var diehardApi: DiehardApi
        get() {
            if (_diehardApi == null) {
                val token = authTokenStorage.load() ?: OAuthToken.empty()
                val uid = userProfileStorage.load()?.uid ?: Uid.empty()
                _diehardApi = DiehardApi(
                    token,
                    uid,
                    resources,
                    networkConfig,
                    testing = false,
                )
            }
            return _diehardApi!!
        }
        private set(value) {
            _diehardApi = value
        }

    fun resetApi() {
        _payApi = null
        _diehardApi = null
    }

    private val reducers: List<Reducer> = listOf(
        SetupReducer(),
        NavigationReducer(),
        UserCardsReducer(),
        AuthorizationReducer(),
        CheckoutReducer(),
        GeneralReducer(),
    )

    private val middleware: List<Middleware> = listOf(
        TracingMiddleware(config.logging) { Log.d("TRACING_MIDDLEWARE", it) },
        SetupMiddleware(),
        AuthorizationMiddleware(
            authTokenStorage,
            userProfileStorage,
            avatarLoader,
            currentCardStorage
        ),
        UserCardsMiddleware(metrica),
        NavigationMiddleware(),
        CurrentCardMiddleware(currentCardChanger),
    )

    fun buildStore(): Store = Store(AppState(), reducers, middleware)
}

