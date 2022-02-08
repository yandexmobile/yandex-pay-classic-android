package com.yandex.pay.core.ui.views

import android.text.method.MovementMethod
import android.widget.TextView
import com.yandex.pay.core.ui.views.interfaces.ILicenseAgreementView

internal class LicenseAgreementView(private val textView: TextView) : ILicenseAgreementView {
    override var text: CharSequence
        get() = textView.text
        set(value) {
            textView.text = value
        }

    override var movementMethod: MovementMethod
        get() = textView.movementMethod
        set(value) {
            textView.movementMethod = value
        }
}
