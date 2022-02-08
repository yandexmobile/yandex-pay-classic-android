package com.yandex.pay.core.data

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize

@Parcelize
@JvmInline
value class GatewayMerchantID private constructor(internal val value: String) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString()!!)

    companion object : Parceler<GatewayMerchantID> {
        @JvmStatic
        fun from(value: String): GatewayMerchantID = GatewayMerchantID(value)

        override fun GatewayMerchantID.write(parcel: Parcel, flags: Int) {
            parcel.writeString(value)
        }

        override fun create(parcel: Parcel): GatewayMerchantID = GatewayMerchantID(parcel)
    }
}
