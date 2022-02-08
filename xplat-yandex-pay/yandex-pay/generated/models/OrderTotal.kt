// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM models/order-total.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class OrderTotal(val amount: Price, val label: String?) {
    open fun toMapJSONItem(): MapJSONItem {
        return MapJSONItem().putString("amount", this.amount).putStringIfPresent("label", this.label)
    }

}

