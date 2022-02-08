package com.yandex.pay.core.utils

import com.yandex.xplat.common.YSError
import com.yandex.xplat.yandex.pay.APIError
import com.yandex.xplat.yandex.pay.TransportError
import com.yandex.xplat.yandex.pay.TransportErrorCodes

internal open class XPlatApiException(message: String) : Exception(message) {
    internal companion object {
        fun from(error: YSError): XPlatApiException =
            if (error.isAuthorizationError()) {
                AuthorizationException(error.message)
            } else {
                XPlatApiException(error.message)
            }
    }
}

internal fun YSError.toXPlatApiException() = XPlatApiException.from(this)

internal fun YSError.isAuthorizationError(): Boolean = when (this) {
    is APIError -> code == 401 || code == 403
    is TransportError -> code == TransportErrorCodes.badStatusCode &&
        (message.endsWith(": 401") || message.endsWith(": 403"))
    else -> false
}
