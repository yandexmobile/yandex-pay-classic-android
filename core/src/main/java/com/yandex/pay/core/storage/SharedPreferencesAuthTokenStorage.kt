package com.yandex.pay.core.storage

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.core.content.edit
import com.yandex.pay.core.data.OAuthToken

internal class SharedPreferencesAuthTokenStorage(
    private val sharedPreferences: SharedPreferences,
) : AuthTokenStorage {
    override fun load(): OAuthToken? =
        sharedPreferences.getString(TOKEN_KEY, null)?.let(::OAuthToken)

    @SuppressLint("ApplySharedPref")
    override fun save(value: OAuthToken) {
        sharedPreferences.edit { putString(TOKEN_KEY, value.value) }
    }

    @SuppressLint("ApplySharedPref")
    override fun drop() {
        sharedPreferences.edit { remove(TOKEN_KEY) }
    }

    private companion object {
        const val TOKEN_KEY: String = "YANDEXPAY_OAUTH_TOKEN"
    }
}
