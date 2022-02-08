package com.yandex.pay.core.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@JvmInline
@Parcelize
internal value class CardID private constructor(internal val value: String) : Parcelable {
    internal companion object {
        fun from(value: String): CardID = CardID(value)
    }
}
