// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM utils/helpers.ts >>>

package com.yandex.xplat.common


public fun <T> requireNotNull(value: T?, error: YSError): T {
    if (value == null) {
        throw error
    }
    return value!!
}

