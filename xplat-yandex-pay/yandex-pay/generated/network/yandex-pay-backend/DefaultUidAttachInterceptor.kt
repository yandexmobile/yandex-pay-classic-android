// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/yandex-pay-backend/default-uid-attach-interceptor.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class DefaultUidAttachInterceptor(private val defaultUid: Int?): NetworkInterceptor {
    open override fun intercept(originalRequest: NetworkRequest): XPromise<NetworkRequest> {
        return resolve(SealedNetworkRequest(originalRequest.method(), originalRequest.targetPath(), originalRequest.params(), this.updateUrlExtra(originalRequest.urlExtra()), originalRequest.headersExtra(), originalRequest.encoding()))
    }

    private fun updateUrlExtra(urlExtra: NetworkUrlExtra): NetworkHeadersExtra {
        return urlExtra.putInt32IfPresent("default_uid", this.defaultUid)
    }

}

