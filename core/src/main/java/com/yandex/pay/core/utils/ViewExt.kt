package com.yandex.pay.core.utils

import android.view.View

internal fun View.show() {
    visible = true
}

internal fun View.hide() {
    visible = false
}

internal var View.visible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }
