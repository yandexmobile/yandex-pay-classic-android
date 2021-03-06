// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/diehard/diehard-status3ds-response.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class DiehardStatus3dsResponse(status: String, statusCode: String?, statusDescription: String?, val status3ds: String?): DiehardResponse(status, statusCode, statusDescription) {
    companion object {
        @JvmStatic
        open fun status3dsFromJsonItem(item: JSONItem): Result<DiehardStatus3dsResponse> {
            return decodeJSONItem(item, __LBL__DiehardStatus3dsResponse_1@ {
                json ->
                val parent = DiehardResponse.baseFromJsonItem(json).tryGetValue()
                val map = json.tryCastAsMapJSONItem()
                val status3ds = map.getString("status_3ds")
                return@__LBL__DiehardStatus3dsResponse_1 DiehardStatus3dsResponse(parent.status, parent.statusCode, parent.statusDescription, status3ds)
            })
        }

    }
}

