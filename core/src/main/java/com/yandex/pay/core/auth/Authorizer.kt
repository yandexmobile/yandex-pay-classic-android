package com.yandex.pay.core.auth

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.authsdk.internal.Constants
import com.yandex.pay.core.BuildConfig
import com.yandex.pay.core.R
import com.yandex.pay.core.data.OAuthToken
import com.yandex.pay.core.storage.AuthTokenStorage

internal class Authorizer(
    fragment: Fragment,
    private val logging: Boolean,
    private val useTestEnvironment: Boolean,
    private val storage: AuthTokenStorage,
    private val authorizationCallback: (Payload) -> Unit,
) {
    class Payload(val result: Result<OAuthToken?>, val fromSDK: Boolean) {
        operator fun component1(): Result<OAuthToken?> = result
        operator fun component2(): Boolean = fromSDK
    }

    private val authSdk: YandexAuthSdk by lazy {
        val context = fragment.requireContext()

        // patchAuthSDKForEnvironment(context)
        YandexAuthSdk(
            context,
            YandexAuthOptions.Builder(context)
                .apply { if (logging) enableLogging() }.build()
        )
    }

    private val authSdkActivityLauncher = fragment.registerForActivityResult(
        @Suppress("MoveLambdaOutsideParentheses")
        AuthorizationResultContract({ authSdk }),
    ) { result ->
        result.onSuccess { token ->
            if (token != null) {
                storage.save(token)
            } else {
                storage.drop()
            }
        }
        sendOutToken(result, true)
    }

    @MainThread
    fun authorize(context: Context): Boolean {
        val token = storage.load()
        return if (token != null) {
            sendOutToken(Result.success(token), false)
            false
        } else {
            authorizeWithSdk(context)
            true
        }
    }

    private fun authorizeWithSdk(context: Context) {
        authSdkActivityLauncher.launch(
            context.resources.getStringArray(R.array.yandexpay_authsdk_scopes).toSet()
        )
    }

    private fun sendOutToken(result: Result<OAuthToken?>, withSDK: Boolean) {
        authorizationCallback(Payload(result, withSDK))
    }

    private fun patchAuthSDKForEnvironment(context: Context) {
        if (!BuildConfig.DEBUG) {
            // We do not rely on config values of Authorizer, because they are decided at runtime.
            return
        }

        val appInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        // Hack to switch AuthSDK to test server.
        val oauthHost =
            context.getString(if (useTestEnvironment) R.string.yandexpay_authsdk_stage_url_string else R.string.yandexpay_authsdk_prod_url_string)
        appInfo.metaData.putString(Constants.META_OAUTH_HOST, oauthHost)
    }
}
