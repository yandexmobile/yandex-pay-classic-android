// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/yandex-pay-backend/set-default-card-response.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class SetDefaultCardResponse(status: String, code: Int): BaseNetworkResponse(status, code) {
    companion object {
        @JvmStatic
        open fun fromJSONItem(json: JSONItem): Result<SetDefaultCardResponse> {
            return BaseNetworkResponse.fromJSON(json, "SetDefaultCard").map( {
                baseResponse ->
                SetDefaultCardResponse(baseResponse.status, baseResponse.code)
            })
        }

    }
}

