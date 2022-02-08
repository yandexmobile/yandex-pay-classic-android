package com.yandex.pay.core.utils

import android.content.res.Resources
import com.yandex.pay.core.data.Error

class YandexPayLibException internal constructor(message: String?, inner: Throwable?) :
    Exception(message, inner) {
    internal companion object {
        fun from(error: Throwable): YandexPayLibException =
            YandexPayLibException(error.message, error)

        fun from(error: Error, resources: Resources): YandexPayLibException? =
            error.code.descriptionResourceID?.let {
                YandexPayLibException(
                    resources.getString(it),
                    null
                )
            }
    }
}
