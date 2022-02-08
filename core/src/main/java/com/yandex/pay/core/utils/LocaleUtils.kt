package com.yandex.pay.core.utils

import android.content.res.Configuration
import android.os.Build
import java.util.*

internal val Configuration.currentLocale: Locale
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        locales[0]
    } else {
        @Suppress("DEPRECATION")
        locale
    }
