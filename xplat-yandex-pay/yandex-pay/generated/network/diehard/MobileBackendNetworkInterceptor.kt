// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/diehard/mobile-backend-network-interceptor.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class MobileBackendNetworkInterceptor(private val authorizationProvider: () -> XPromise<MobileBackendAuthorization?>, private val serviceToken: String): NetworkInterceptor {
    open override fun intercept(originalRequest: NetworkRequest): XPromise<NetworkRequest> {
        return this.authorizationProvider().then( {
            authorization ->
            SealedNetworkRequest(originalRequest.method(), originalRequest.targetPath(), originalRequest.params(), originalRequest.urlExtra(), this.updateHeadersExtra(originalRequest.headersExtra(), authorization), originalRequest.encoding())
        })
    }

    private fun updateHeadersExtra(headersExtra: NetworkHeadersExtra, authorization: MobileBackendAuthorization?): NetworkHeadersExtra {
        val headers = headersExtra.putString("X-Service-Token", this.serviceToken)
        if (authorization != null) {
            headers.putString("Authorization", "OAuth ${authorization!!.oauthToken}")
            headers.putString("X-Uid", authorization!!.uid)
        }
        return headers
    }

    companion object {
        @JvmStatic
        open fun create(oauthToken: String?, serviceToken: String, uid: String?): MobileBackendNetworkInterceptor {
            val authorization = MobileBackendAuthorization.fromAuthorizationPair(oauthToken, uid)
            return MobileBackendNetworkInterceptor( {
                 ->
                toPromise(authorization)
            }
, serviceToken)
        }

    }
}

