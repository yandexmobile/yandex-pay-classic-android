// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/yandex-pay-backend/pay-mobile-auth-interceptor.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class PayMobileAuthInterceptor(private val oauthToken: String): NetworkInterceptor {
    open override fun intercept(originalRequest: NetworkRequest): XPromise<NetworkRequest> {
        return resolve(SealedNetworkRequest(originalRequest.method(), originalRequest.targetPath(), originalRequest.params(), originalRequest.urlExtra(), this.updateHeadersExtra(originalRequest.headersExtra()), originalRequest.encoding()))
    }

    private fun updateHeadersExtra(headersExtra: NetworkHeadersExtra): NetworkHeadersExtra {
        return if (isStringNullOrEmpty(this.oauthToken)) headersExtra else headersExtra.putString("Authorization", "OAuth ${this.oauthToken}")
    }

}

