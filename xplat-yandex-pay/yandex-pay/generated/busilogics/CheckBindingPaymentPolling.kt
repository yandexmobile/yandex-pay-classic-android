// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM busilogics/check-binding-payment-polling.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public interface CheckBindingPaymentPolling {
    fun checkResponse(response: CheckBindingPaymentResponse): Result<PollingStep>
}

public open class CheckBindingPaymentPollingHandler(private val callback: ChallengeCallback): CheckBindingPaymentPolling {
    private var receivedRedirect: Boolean = false
    private var received3dsStatus: Boolean = false
    open override fun checkResponse(response: CheckBindingPaymentResponse): Result<PollingStep> {
        when (response.status) {
            "success" -> {
                return resultValue(PollingStep.done)
            }
            "wait_for_notification" -> {
                try {
                    if (response.redirectUrl != null && !this.receivedRedirect) {
                        this.receivedRedirect = true
                        val uri = Uris.fromString(response.redirectUrl!!)
                        if (uri == null) {
                            return resultError(CardBindingServiceError.challengeInvalidRedirectUrl(response))
                        }
                        this.callback.show3ds(uri!!)
                    }
                    val success = response.status3ds == "success"
                    if (!this.received3dsStatus && (success || response.status3ds == "failed")) {
                        this.received3dsStatus = true
                        this.callback.hide3ds(success)
                    }
                    return resultValue(PollingStep.retry)
                } catch (e: RuntimeException) {
                    return resultError(CardBindingServiceError.challengeHandlingError(response, e))
                }
            }
            else -> {
                return resultError(CardBindingServiceError.undefinedStatus(response))
            }
        }
    }

}

