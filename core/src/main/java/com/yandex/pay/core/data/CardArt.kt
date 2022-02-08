package com.yandex.pay.core.data

import android.net.Uri
import com.yandex.xplat.yandex.pay.CardArt as XCardArt

internal class CardArt private constructor(val pictures: Map<String, Uri>) {
    val original: Uri?
        get() = pictures["original"]

    internal val xplat: XCardArt
        get() = XCardArt(pictures.mapValues { it.value.toString() }.toMutableMap())

    internal companion object {
        fun from(xplat: XCardArt): CardArt = CardArt(
            xplat.pictures.mapNotNull { (key, value) ->
                Uri.parse(value)?.let { key to it }
            }.toMap()
        )

        val empty: CardArt
            get() = CardArt(emptyMap())
    }
}
