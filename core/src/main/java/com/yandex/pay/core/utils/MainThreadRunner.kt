package com.yandex.pay.core.utils

import android.os.Handler
import android.os.Looper

internal class MainThreadRunner : Runner {
    private val mainThreadHandler: Handler = Handler(Looper.getMainLooper())

    override operator fun invoke(action: () -> Unit) {
        mainThreadHandler.post(action)
    }
}
