package com.yandex.pay.core.data

import android.os.Parcelable
import com.yandex.xplat.yandex.pay.OrderTotal
import kotlinx.parcelize.Parcelize
import com.yandex.xplat.yandex.pay.Order as XOrder

@Parcelize
class Order(
    val id: OrderID,
    val amount: Price,
    val label: String?,
    val items: List<OrderItem>,
) : Parcelable {
    internal val xplat: XOrder
        get() = XOrder(id.value,
            OrderTotal(amount.xplat, label),
            items.map(OrderItem::xplat).toMutableList())

    internal companion object {
        fun from(xplat: XOrder): Order = Order(
            OrderID.from(xplat.id),
            Price.from(xplat.total.amount),
            xplat.total.label,
            xplat.items.map(OrderItem.Companion::from),
        )
    }
}
