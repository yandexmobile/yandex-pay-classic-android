package com.yandex.pay.core.data

import com.yandex.pay.core.utils.ErrorDescriptor

internal sealed interface ErrorType {
    class Fatal(val error: Error) : ErrorType
    class Recoverable(val error: ErrorDescriptor) : ErrorType
}
