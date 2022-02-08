package com.yandex.pay.core.ui.views.presenters

import com.yandex.pay.core.ui.views.interfaces.IBindCardButtonView
import com.yandex.pay.core.ui.views.interfaces.Presenter
import com.yandex.pay.core.ui.views.interfaces.updating

internal class BindCardButtonPresenter :
    Presenter<BindCardButtonPresenter.Payload, IBindCardButtonView> {
    internal sealed class Payload(val title: String) {
        class Normal(title: String, val enabled: Boolean, val onClick: () -> Unit) : Payload(title)
        class Error(title: String) : Payload(title)
        class Progress(title: String) : Payload(title)
        class Done(title: String) : Payload(title)
    }

    override fun present(data: Payload, view: IBindCardButtonView) {
        when (data) {
            is Payload.Normal -> present(data, view)
            is Payload.Error -> present(data, view)
            is Payload.Progress -> present(data, view)
            is Payload.Done -> present(data, view)
        }
    }

    private fun present(data: Payload.Normal, view: IBindCardButtonView) {
        view.updating {
            title = data.title
            disabled = !data.enabled
            onClick = data.onClick
            state = IBindCardButtonView.State.NORMAL
        }
    }

    private fun present(data: Payload.Error, view: IBindCardButtonView) {
        view.updating {
            title = data.title
            disabled = true
            onClick = {}
            state = IBindCardButtonView.State.ERROR
        }
    }

    private fun present(data: Payload.Progress, view: IBindCardButtonView) {
        view.updating {
            title = data.title
            disabled = true
            onClick = {}
            state = IBindCardButtonView.State.PROGRESS
        }
    }

    private fun present(data: Payload.Done, view: IBindCardButtonView) {
        view.updating {
            title = data.title
            disabled = true
            onClick = {}
            state = IBindCardButtonView.State.DONE
        }
    }
}
