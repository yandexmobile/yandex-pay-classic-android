package com.yandex.pay.core.userprofile

import android.content.Context
import android.net.Uri
import androidx.core.content.res.ResourcesCompat
import com.yandex.pay.core.R
import com.yandex.pay.core.XPlatApi
import com.yandex.pay.core.data.UserProfile
import com.yandex.pay.core.di.ComponentsHolder
import com.yandex.pay.core.storage.UserProfileStorage

internal class UserProfileLoader(
    private val context: Context,
    private val components: ComponentsHolder,
) {
    internal interface LoadingProgress {
        fun onUnresolvedProfileLoaded(profile: UserProfile.Unresolved)
        fun onResolvedProfileLoaded(profile: UserProfile.Resolved)
        fun onFailure(error: Throwable)
    }

    private val storage: UserProfileStorage
        get() = components.userProfileStorage

    private val avatarLoader: AvatarLoader
        get() = components.avatarLoader

    private val api: XPlatApi
        get() = components.payApi

    fun loadFromStorage(): UserProfile? {
        val unresolved = storage.load() ?: return null
        val image = avatarLoader.loadFromFileSystem()
        return image?.let { unresolved.toResolved(it, false) } ?: unresolved
    }

    fun loadFromNetwork(progress: LoadingProgress) {
        api.loadAvatar { result ->
            result
                .onSuccess { profile ->
                    storage.save(profile)
                    progress.onUnresolvedProfileLoaded(profile)
                    val url = networkAvatarUrl(profile)
                    if (url == null) {
                        loadPlaceholder(profile, progress)
                    } else {
                        startNetworkImageLoading(profile, url, progress)
                    }
                }
                .onFailure(progress::onFailure)
        }
    }

    private fun startNetworkImageLoading(
        profile: UserProfile.Unresolved,
        url: Uri,
        progress: LoadingProgress,
    ) {
        avatarLoader.loadFromNetwork(url) { image ->
            if (image != null) {
                progress.onResolvedProfileLoaded(profile.toResolved(image, false))
            } else {
                loadPlaceholder(profile, progress)
            }
        }
    }

    private fun loadPlaceholder(profile: UserProfile.Unresolved, progress: LoadingProgress) {
        val image = ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.yandexpay_ic_avatar_placeholder,
            context.theme
        )!!
        progress.onResolvedProfileLoaded(profile.toResolved(image, true))
    }

    private fun networkAvatarUrl(
        profile: UserProfile.Unresolved,
    ): Uri? =
        if (context.resources.getBoolean(R.bool.yandexpay_use_hidpi_avatars)) profile.hiDpiUrl
        else profile.loDpiUrl
}
