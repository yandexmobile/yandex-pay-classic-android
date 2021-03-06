// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/diehard/encrypted-card-request.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class EncryptedCardRequest(val id: String, val cardID: String, val cvn: String): DiehardRequest() {
    open override fun method(): NetworkMethod {
        return NetworkMethod.post
    }

    open override fun targetPath(): String {
        return "cp/inbound/api/v1/yandex-pay/wallet/thales/encrypted-card"
    }

    protected open override fun wrappedParams(): NetworkParams {
        return MapJSONItem().putString("card_id", this.cardID).putString("cvn", this.cvn)
    }

    open override fun encoding(): RequestEncoding {
        return JsonRequestEncoding()
    }

    open override fun headersExtra(): NetworkHeadersExtra {
        return MapJSONItem().putString("x-request-id", this.id)
    }

}

