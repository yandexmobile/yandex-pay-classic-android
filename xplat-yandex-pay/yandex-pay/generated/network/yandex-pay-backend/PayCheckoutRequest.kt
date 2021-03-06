// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/yandex-pay-backend/pay-checkout-request.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class PayCheckoutRequest(val cardId: String, val merchantOrigin: String, val paymentSheet: PaymentSheet): BaseNetworkRequest() {
    open override fun method(): NetworkMethod {
        return NetworkMethod.post
    }

    open override fun targetPath(): String {
        return "api/mobile/v1/checkout"
    }

    open override fun params(): NetworkParams {
        return MapJSONItem().putString("card_id", this.cardId).putString("merchant_origin", this.merchantOrigin).put("sheet", this.paymentSheet.toJSON(true))
    }

    open override fun encoding(): RequestEncoding {
        return JsonRequestEncoding()
    }

}

