// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/diehard/verify-binding-response.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class VerifyBindingResponse(val purchaseToken: String) {
    companion object {
        @JvmStatic
        open fun fromJsonItem(item: JSONItem): Result<VerifyBindingResponse> {
            return decodeJSONItem(item, __LBL__VerifyBindingResponse_1@ {
                json ->
                val map = json.tryCastAsMapJSONItem()
                val purchaseToken = map.tryGetString("purchase_token")
                return@__LBL__VerifyBindingResponse_1 VerifyBindingResponse(purchaseToken)
            })
        }

    }
}

