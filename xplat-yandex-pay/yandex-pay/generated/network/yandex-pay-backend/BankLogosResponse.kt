// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/yandex-pay-backend/bank-logos-response.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class BankLogosResponse(status: String, code: Int, val logos: YSMap<String, String>): BaseNetworkResponse(status, code) {
    companion object {
        @JvmStatic
        open fun fromJSONItem(json: JSONItem): Result<BankLogosResponse> {
            return decodeJSONItem(json, __LBL__BankLogosResponse_1@ {
                item ->
                val map = item.tryCastAsMapJSONItem().asMap()
                val result = mutableMapOf<String, String>()
                map.__forEach(__LBL__BankLogosResponse_2@ {
                    value, key ->
                    val stringValue = undefinedToNull(value.castAsStringJSONItem()?.value)
                    if (stringValue != null) {
                        result.set(key, stringValue)
                    }
                })
                return@__LBL__BankLogosResponse_1 BankLogosResponse("success", 200, result)
            })
        }

    }
}
