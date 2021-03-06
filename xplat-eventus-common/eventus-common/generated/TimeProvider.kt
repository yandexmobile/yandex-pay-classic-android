// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM time-provider.ts >>>

package com.yandex.xplat.eventus.common

import com.yandex.xplat.common.*

public interface TimeProvider {
    fun getCurrentTimeMs(): Long
}

public open class NativeTimeProvider: TimeProvider {
    open override fun getCurrentTimeMs(): Long {
        return int64(YSDate.now())
    }

}

