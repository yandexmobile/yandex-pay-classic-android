package com.yandex.pay.core.navigation

import android.content.Context
import android.content.Intent
import com.yandex.pay.core.ui.MainActivity

internal object ActivityRoutes {
    fun mainActivityIntent(context: Context): Intent = MainActivity.createIntent(context)
}
