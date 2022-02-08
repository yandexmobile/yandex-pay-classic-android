package com.yandex.pay.core.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
@JvmInline
value class Gateway private constructor(internal val value: String) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()!!)

    companion object : Parceler<Gateway> {
        @JvmStatic
        fun from(value: String): Gateway = Gateway(value)

        override fun Gateway.write(parcel: Parcel, flags: Int) {
            parcel.writeString(value)
        }

        override fun create(parcel: Parcel): Gateway = Gateway(parcel)
    }
}
