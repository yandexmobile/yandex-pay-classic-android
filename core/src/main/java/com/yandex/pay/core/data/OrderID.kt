package com.yandex.pay.core.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
@JvmInline
value class OrderID private constructor(internal val value: String) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()!!)

    companion object : Parceler<OrderID> {
        @JvmStatic
        fun from(value: String): OrderID = OrderID(value)

        override fun OrderID.write(parcel: Parcel, flags: Int) {
            parcel.writeString(value)
        }

        override fun create(parcel: Parcel): OrderID = OrderID(parcel)
    }
}
