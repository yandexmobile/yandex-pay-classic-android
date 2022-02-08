package com.yandex.pay.core.userprofile

import com.yandex.pay.core.data.UserProfile
import com.yandex.pay.core.ui.views.presenters.AvatarPresenter

internal class UserProfileDataSource(
    private val loader: UserProfileLoader,
    private val onAvatarTapped: () -> Unit = {},
) {
    private var loadingAvatar: Boolean = false

    fun fetch(callback: (AvatarPresenter.Payload) -> Unit) {
        if (loadingAvatar) {
            return
        }
        loadingAvatar = true

        val avatarPayload = when (val profile = loader.loadFromStorage()) {
            null -> AvatarPresenter.Payload.Loading
            is UserProfile.Resolved -> AvatarPresenter.Payload.Data(
                profile,
                true,
                ::onAvatarTapped.get()
            )
            is UserProfile.Unresolved -> AvatarPresenter.Payload.Loading
        }

        callback(avatarPayload)
        loader.loadFromNetwork(object : UserProfileLoader.LoadingProgress {
            override fun onUnresolvedProfileLoaded(profile: UserProfile.Unresolved) = Unit

            override fun onResolvedProfileLoaded(profile: UserProfile.Resolved) {
                callback(
                    AvatarPresenter.Payload.Data(
                        profile,
                        true,
                        ::onAvatarTapped.get()
                    ),
                )
                loadingAvatar = false
            }

            override fun onFailure(error: Throwable) {
                loadingAvatar = false
            }
        })
    }
}
