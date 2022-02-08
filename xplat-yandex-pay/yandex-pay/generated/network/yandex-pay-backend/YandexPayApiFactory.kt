// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/yandex-pay-backend/yandex-pay-api-factory.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class YandexPayApiFactory(private val network: Network, private val serializer: JSONSerializer) {
    open fun createForUser(oauthToken: String, defaultUid: Int? = null): YandexPayApi {
        val authInterceptor = PayMobileAuthInterceptor(oauthToken)
        val defaultUidInterceptor = DefaultUidAttachInterceptor(defaultUid)
        val authorizedNetwork = NetworkIntermediate(this.network, mutableListOf(authInterceptor, defaultUidInterceptor))
        return YandexPayApi(authorizedNetwork, ResponseProcessor(this.serializer))
    }

}
