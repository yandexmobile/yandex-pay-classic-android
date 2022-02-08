package com.yandex.pay.core.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import com.yandex.xplat.yandex.pay.Price as XPrice

@Parcelize
@JvmInline
value class Price private constructor(internal val value: String) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()!!)

    internal val xplat: XPrice
        get() = value

    companion object : Parceler<Price> {
        @JvmStatic
        fun from(value: String): Price = Price(value)

        override fun Price.write(parcel: Parcel, flags: Int) {
            parcel.writeString(value)
        }

        override fun create(parcel: Parcel): Price = Price(parcel)
    }
}
