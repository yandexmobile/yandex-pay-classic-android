// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/yandex-pay-backend/register-push-token-response.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class RegisterPushTokenResponse(status: String, code: Int): BaseNetworkResponse(status, code) {
    companion object {
        @JvmStatic
        open fun fromJSONItem(json: JSONItem): Result<RegisterPushTokenResponse> {
            return BaseNetworkResponse.fromJSON(json, "RegisterPushToken").map( {
                baseResponse ->
                RegisterPushTokenResponse(baseResponse.status, baseResponse.code)
            })
        }

    }
}

