// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/yandex-pay-backend/sync-user-card-response.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class SyncUserCardResponse(status: String, code: Int, val card: UserCard): BaseNetworkResponse(status, code) {
    companion object {
        @JvmStatic
        open fun fromJSONItem(json: JSONItem): Result<SyncUserCardResponse> {
            return BaseNetworkResponse.fromJSON(json, "SyncUserCard").flatMap( {
                baseResponse ->
                decodeJSONItem(json, __LBL__SyncUserCardResponse_1@ {
                    item ->
                    val map = item.tryCastAsMapJSONItem()
                    val data = map.tryGet("data").tryCastAsMapJSONItem()
                    val card = UserCard.fromJSONItem(data).tryGetValue()
                    return@__LBL__SyncUserCardResponse_1 SyncUserCardResponse(baseResponse.status, baseResponse.code, card)
                })
            })
        }

    }
}
