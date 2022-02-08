package com.yandex.pay.core.utils

import com.yandex.pay.core.data.ValidationResult

internal class ValidationException(val validationResult: ValidationResult) :
    XPlatApiException("Validation failed")
