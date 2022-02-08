package com.yandex.pay.core.data

import com.yandex.pay.core.R
import com.yandex.xplat.yandex.pay.ErrorCodes

/**
 * Validation procedure status.
 */
enum class ValidationResult {
    OK,
    UnknownMerchantOrigin,
    UnknownMerchant,
    UnknownGateway,
    InsecureMerchantOrigin,
    AmountLimitExceeded,
    InvalidAmount,
    InvalidCountry,
    InvalidCurrency,
    AmountMismatch,
    UnknownValidationProblem,
    AuthenticationProblem;

    val descriptionResourceID: Int?
        get() = when (this) {
            OK -> null
            UnknownMerchantOrigin -> R.string.yandexpay_unknown_merchant_origin
            UnknownMerchant -> R.string.yandexpay_unknown_merchant
            UnknownGateway -> R.string.yandexpay_unknown_gateway
            InsecureMerchantOrigin -> R.string.yandexpay_insecure_merchant_origin
            AmountLimitExceeded -> R.string.yandexpay_amount_limit_exceeded
            InvalidAmount -> R.string.yandexpay_invalid_amount
            InvalidCountry -> R.string.yandexpay_invalid_country
            InvalidCurrency -> R.string.yandexpay_invalid_currency
            AmountMismatch -> R.string.yandexpay_amount_mismatch
            UnknownValidationProblem -> R.string.yandexpay_unknown_validation_problem
            AuthenticationProblem -> R.string.yandexpay_auth_failed_error
        }

    internal companion object {
        fun fromErrorCode(errorCode: ErrorCodes): ValidationResult = when (errorCode) {
            ErrorCodes.amountLimitExceeded -> AmountLimitExceeded
            ErrorCodes.amountMismatch -> AmountMismatch
            ErrorCodes.gatewayNotFound -> UnknownGateway
            ErrorCodes.insecureMerchantOrigin -> InsecureMerchantOrigin
            ErrorCodes.invalidAmount -> InvalidAmount
            ErrorCodes.invalidCountry -> InvalidCountry
            ErrorCodes.invalidCurrency -> InvalidCurrency
            ErrorCodes.merchantNotFound -> UnknownMerchant
            ErrorCodes.merchantOriginError -> UnknownMerchantOrigin

            ErrorCodes.cardNetworkNotSupported, ErrorCodes.cardNotFound, ErrorCodes.codeCheckFailed, ErrorCodes.invalidVersion ->
                UnknownValidationProblem
        }
    }
}
