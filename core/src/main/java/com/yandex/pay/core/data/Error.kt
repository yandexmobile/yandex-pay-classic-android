package com.yandex.pay.core.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Error(val code: ValidationResult) : Parcelable
