package com.yandex.pay.core.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
@JvmInline
value class MerchantID private constructor(internal val value: String) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()!!)

    companion object : Parceler<MerchantID> {
        @JvmStatic
        fun from(value: String): MerchantID = MerchantID(value)

        override fun MerchantID.write(parcel: Parcel, flags: Int) {
            parcel.writeString(value)
        }

        override fun create(parcel: Parcel): MerchantID = MerchantID(parcel)
    }
}
