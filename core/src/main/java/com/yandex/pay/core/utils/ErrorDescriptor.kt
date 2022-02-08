package com.yandex.pay.core.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@JvmInline
internal value class ErrorDescriptor(val message: String?) : Parcelable
