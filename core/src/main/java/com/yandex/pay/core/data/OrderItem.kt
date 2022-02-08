package com.yandex.pay.core.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.yandex.xplat.yandex.pay.OrderItem as XOrderItem

@Parcelize
class OrderItem(val amount: Price, val label: String?) : Parcelable {
    internal val xplat: XOrderItem
        get() = XOrderItem(amount.value, label)

    internal companion object {
        fun from(xplat: XOrderItem): OrderItem = OrderItem(
            Price.from(xplat.amount),
            xplat.label,
        )
    }
}
