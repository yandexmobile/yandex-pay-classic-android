package com.yandex.pay.core.ui.views.presenters

import com.yandex.pay.core.data.UserProfile
import com.yandex.pay.core.ui.views.interfaces.IAvatarView
import com.yandex.pay.core.ui.views.interfaces.Presenter
import com.yandex.pay.core.ui.views.interfaces.updating

internal class AvatarPresenter : Presenter<AvatarPresenter.Payload, IAvatarView> {
    sealed interface Payload {
        object Loading : Payload
        class Data(
            val profile: UserProfile.Resolved,
            val enabled: Boolean,
            val onClick: () -> Unit
        ) : Payload
    }

    override fun present(data: Payload, view: IAvatarView) {
        when (data) {
            is Payload.Loading -> present(data, view)
            is Payload.Data -> present(data, view)
        }
    }

    private fun present(data: Payload.Data, view: IAvatarView) {
        val profile = data.profile

        view.updating {
            name = profile.name
            disabled = !data.enabled
            image = profile.image
            onClick = data.onClick
        }
    }

    private fun present(data: Payload.Loading, view: IAvatarView) {
        view.updating {
            name = ""
            disabled = true
            image = null
            onClick = {}
        }
    }
}
