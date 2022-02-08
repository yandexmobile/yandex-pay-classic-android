package com.yandex.pay.core.storage

import android.content.SharedPreferences
import android.net.Uri
import androidx.core.content.edit
import com.yandex.pay.core.data.Uid
import com.yandex.pay.core.data.UserProfile

internal class SharedPreferencesUserProfileStorage(private val sharedPreferences: SharedPreferences): UserProfileStorage {
    override fun load(): UserProfile.Unresolved? {
        val name = sharedPreferences.getString(USER_NAME, null) ?: return null
        val uid = sharedPreferences.getString(UID, null) ?: return null
        val loDpiUrl = sharedPreferences.getString(LODPI_AVATAR_URL, null) ?: return null
        val hiDpiUrl = sharedPreferences.getString(HIDPI_AVATAR_URL, null) ?: return null
        return UserProfile.Unresolved(name, Uid(uid), Uri.parse(loDpiUrl), Uri.parse(hiDpiUrl))
    }

    override fun save(value: UserProfile.Unresolved) {
        sharedPreferences.edit {
            putString(USER_NAME, value.name)
            putString(UID, value.uid.value)
            putString(LODPI_AVATAR_URL, value.loDpiUrl.toString())
            putString(HIDPI_AVATAR_URL, value.hiDpiUrl.toString())
        }
    }

    override fun drop() {
        sharedPreferences.edit {
            remove(USER_NAME)
            remove(UID)
            remove(LODPI_AVATAR_URL)
            remove(HIDPI_AVATAR_URL)
        }
    }

    private companion object {
        const val USER_NAME: String = "YANDEXPAY_USER_NAME"
        const val UID: String = "YANDEX_UID"
        const val LODPI_AVATAR_URL: String = "YANDEXPAY_LODPI_URL"
        const val HIDPI_AVATAR_URL: String = "YANDEXPAY_HIDPI_URL"
    }
}
