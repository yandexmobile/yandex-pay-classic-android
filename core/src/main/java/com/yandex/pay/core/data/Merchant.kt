package com.yandex.pay.core.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.yandex.xplat.yandex.pay.Merchant as XMerchant

@Parcelize
class Merchant(
    val id: MerchantID,
    val name: String,
    val origin: String,
) : Parcelable {
    internal val xplat: XMerchant
        get() = XMerchant(id.value, name)

    internal companion object {
        fun from(xplat: XMerchant, origin: String): Merchant = Merchant(
            MerchantID.from(xplat.id),
            xplat.name,
            origin,
        )
    }
}
