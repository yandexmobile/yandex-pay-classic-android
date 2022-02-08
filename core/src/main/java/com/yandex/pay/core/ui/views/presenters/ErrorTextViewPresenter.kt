package com.yandex.pay.core.ui.views.presenters

import com.yandex.pay.core.ui.views.interfaces.IErrorTextView
import com.yandex.pay.core.ui.views.interfaces.Presenter
import com.yandex.pay.core.ui.views.interfaces.updating

internal class ErrorTextViewPresenter : Presenter<ErrorTextViewPresenter.Payload, IErrorTextView> {
    internal class Payload(val error: String?)

    override fun present(data: Payload, view: IErrorTextView) {
        view.updating {
            error = data.error
        }
    }
}
