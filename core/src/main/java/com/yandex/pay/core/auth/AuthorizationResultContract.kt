package com.yandex.pay.core.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.pay.core.data.OAuthToken
import com.yandex.pay.core.utils.Provider

internal class AuthorizationResultContract(private val authSdk: Provider<YandexAuthSdk>) :
    ActivityResultContract<Set<String>, Result<OAuthToken?>>() {
    override fun createIntent(context: Context, scopes: Set<String>): Intent =
        authSdk().createLoginIntent(
            YandexAuthLoginOptions.Builder()
                .setScopes(scopes)
                .setForceConfirm(false)
                .build()
        )

    override fun parseResult(resultCode: Int, intent: Intent?): Result<OAuthToken?> =
        if (resultCode == Activity.RESULT_OK) {
            try {
                Result.success(authSdk().extractToken(resultCode, intent)?.let(OAuthToken::from))
            } catch (e: Throwable) {
                Result.failure(e)
            }
        } else {
            Result.success(null)
        }
}
