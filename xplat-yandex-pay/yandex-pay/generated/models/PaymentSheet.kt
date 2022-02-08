// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM models/payment-sheet.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class PaymentSheet(val merchant: Merchant, val order: Order, val currencyCode: CurrencyCodes, val countryCode: CountryCodes, val paymentMethods: YSArray<PaymentMethod>) {
    open fun toJSON(addVersion: Boolean): JSONItem {
        val result = MapJSONItem().putString("country_code", countryCodeToString(this.countryCode)).putString("currency_code", currencyCodeToString(this.currencyCode)).put("merchant", this.merchant.toMapJSONItem()).put("order", this.order.toMapJSONItem()).put("payment_methods", ArrayJSONItem(this.paymentMethods.map( {
            item ->
            item.toMapJSONItem()
        })))
        if (addVersion) {
            result.putInt32("version", 2)
        }
        return result
    }

}
