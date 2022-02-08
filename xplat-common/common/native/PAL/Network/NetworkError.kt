package com.yandex.xplat.common

import java.io.IOException

sealed class NetworkError(message: String, platformError: Throwable? = null) : YSError(message, platformError) {
    class NetworkErrorTransportFailure(message: String, val reason: IOException) : NetworkError(message)
    class NetworkErrorBadCode(val code: Int) : NetworkError("Server responded with code $code")
    object NetworkErrorNoData : NetworkError("No payload in network response")
}
