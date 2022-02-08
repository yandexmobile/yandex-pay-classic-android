package com.yandex.pay.core.ui.views

import android.widget.TextView
import com.yandex.pay.core.ui.views.interfaces.IErrorTextView
import com.yandex.pay.core.utils.hide
import com.yandex.pay.core.utils.show

internal class ErrorTextView(private val textView: TextView) : IErrorTextView {
    override var error: String? = null

    override fun update() {
        if (error == null) {
            textView.hide()
        } else {
            textView.text = error
            textView.show()
        }
    }
}
