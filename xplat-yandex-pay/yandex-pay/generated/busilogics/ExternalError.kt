// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM busilogics/external-error.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class ExternalError(val kind: ExternalErrorKind, val trigger: ExternalErrorTrigger, val code: Int?, val status: String?, val message: String) {
    companion object {
        @JvmStatic
        open fun convert(error: YSError): ExternalError {
            if (error is ExternalConvertibleError) {
                val convertible = error as ExternalConvertibleError
                return convertible.convertToExternalError()
            }
            return ExternalError(ExternalErrorKind.unknown, ExternalErrorTrigger.internal_sdk, null, null, error.message)
        }

    }
}

public open class ExternalConvertibleError(val kind: ExternalErrorKind, val trigger: ExternalErrorTrigger, val code: Int?, val status: String?, message: String): YSError(message) {
    open fun convertToExternalError(): ExternalError {
        return ExternalError(this.kind, this.trigger, this.code, this.status, this.message)
    }

}

public enum class ExternalErrorTrigger {
    internal_sdk,
    mobile_backend,
    diehard,
    nspk,
}
public enum class ExternalErrorKind {
    unknown,
    internal_error,
    authorization,
    network,
    fail_3ds,
    expired_card,
    invalid_processing_request,
    limit_exceeded,
    not_enough_funds,
    payment_authorization_reject,
    payment_cancelled,
    payment_gateway_technical_error,
    payment_timeout,
    promocode_already_used,
    restricted_card,
    transaction_not_permitted,
    user_cancelled,
    card_validation_invalid_argument,
    apple_pay,
    google_pay,
    too_many_cards,
    no_email,
}
public fun mobileBackendStatusToKind(code: Int): ExternalErrorKind {
    if (code == 1004) {
        return ExternalErrorKind.authorization
    }
    return ExternalErrorKind.unknown
}

public fun diehardStatusToKind(response: DiehardStatus3dsResponse): ExternalErrorKind {
    if (response.status == "authorization_reject") {
        return ExternalErrorKind.payment_authorization_reject
    }
    if (response.status == "expired_card") {
        return ExternalErrorKind.expired_card
    }
    if (response.status == "not_enough_funds") {
        return ExternalErrorKind.not_enough_funds
    }
    if (response.status == "fail_3ds" || (response.status == "error" && response.statusCode == "technical_error" && response.status3ds == "failed")) {
        return ExternalErrorKind.fail_3ds
    }
    if (response.status == "invalid_processing_request") {
        return ExternalErrorKind.invalid_processing_request
    }
    if (response.status == "limit_exceeded") {
        return ExternalErrorKind.limit_exceeded
    }
    if (response.status == "payment_timeout") {
        return ExternalErrorKind.payment_timeout
    }
    if (response.status == "promocode_already_used") {
        return ExternalErrorKind.promocode_already_used
    }
    if (response.status == "restricted_card") {
        return ExternalErrorKind.restricted_card
    }
    if (response.status == "payment_gateway_technical_error") {
        return ExternalErrorKind.payment_gateway_technical_error
    }
    if (response.status == "transaction_not_permitted") {
        return ExternalErrorKind.transaction_not_permitted
    }
    if (response.status == "user_cancelled") {
        return ExternalErrorKind.user_cancelled
    }
    if (response.status == "operation_cancelled") {
        return ExternalErrorKind.payment_cancelled
    }
    if (response.status == "too_many_cards" || (response.statusDescription != null && response.statusDescription == "too_many_cards")) {
        return ExternalErrorKind.too_many_cards
    }
    return ExternalErrorKind.unknown
}

