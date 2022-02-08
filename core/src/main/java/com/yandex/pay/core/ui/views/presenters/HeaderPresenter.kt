package com.yandex.pay.core.ui.views.presenters

import com.yandex.pay.core.ui.views.interfaces.IHeaderView
import com.yandex.pay.core.ui.views.interfaces.Presenter
import com.yandex.pay.core.ui.views.interfaces.updating

internal class HeaderPresenter : Presenter<HeaderPresenter.Payload, IHeaderView> {
    sealed interface Payload {
        object Root : Payload
        class BackButton(val onClick: () -> Unit) : Payload
    }

    override fun present(data: Payload, view: IHeaderView) {
        when (data) {
            is Payload.Root -> present(data, view)
            is Payload.BackButton -> present(data, view)
        }
    }

    private fun present(data: Payload.Root, view: IHeaderView) {
        view.updating {
            root = true
            onBackButtonClick = null
        }
    }

    private fun present(data: Payload.BackButton, view: IHeaderView) {
        view.updating {
            root = false
            onBackButtonClick = data.onClick
        }
    }
}
