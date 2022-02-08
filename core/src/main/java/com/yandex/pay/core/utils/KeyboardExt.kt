package com.yandex.pay.core.utils

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

internal object Keyboard {
    fun hide(view: View) {
        inputMethodManager(view).hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun show(view: View) {
        view.post {
            inputMethodManager(view).showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun inputMethodManager(view: View): InputMethodManager =
        view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
}
