// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM network/diehard/diehard-backend-api.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class DiehardBackendApiError(val response: DiehardStatus3dsResponse): NetworkServiceError(diehardStatusToKind(response), ExternalErrorTrigger.diehard, null, "Diehard Error: status - ${response.status}, code - ${response.statusCode ?: "N/A"}, status_3ds - ${response.status3ds ?: "N/A"}, description - ${response.statusDescription ?: "N/A"}") {
    open override fun convertToExternalError(): ExternalError {
        return ExternalError(this.kind, this.trigger, this.code, this.response.status, this.message)
    }

}

public open class DiehardBackendApi(private val networkService: NetworkService) {
    open fun checkBindingPayment(request: CheckPaymentRequest): XPromise<CheckBindingPaymentResponse> {
        return this.networkService.performRequest(request,  {
            item ->
            CheckBindingPaymentResponse.fromJsonItem(item)
        })
    }

    open fun newCardBinding(request: NewCardBindingRequest): XPromise<NewCardBindingResponse> {
        return this.networkService.performRequest(request,  {
            item ->
            NewCardBindingResponse.fromJsonItem(item)
        })
    }

    open fun encryptedCard(request: EncryptedCardRequest): XPromise<EncryptedCardResponse> {
        return this.networkService.performRequest(request,  {
            item ->
            EncryptedCardResponse.fromJSONItem(item)
        })
    }

    companion object {
        @JvmStatic
        open fun create(network: Network, serializer: JSONSerializer, passportToken: String?): DiehardBackendApi {
            val passportInterceptor = PassportHeaderInterceptor(passportToken)
            val authorizedNetwork = NetworkIntermediate(network, mutableListOf(passportInterceptor))
            val errorProcessor = DiehardBackendErrorProcessor()
            val networkService = NetworkService(authorizedNetwork, serializer, errorProcessor)
            return DiehardBackendApi(networkService)
        }

    }
}

public open class DiehardBackendErrorProcessor: NetworkServiceErrorProcessor {
    private val knownValidStatuses: YSSet<String> = arrayToSet(mutableListOf("success", "wait_for_notification", "wait_for_processing"))
    open override fun extractError(errorBody: JSONItem): NetworkServiceError? {
        val response = DiehardStatus3dsResponse.status3dsFromJsonItem(errorBody)
        if (response.isError()) {
            return null
        }
        return DiehardBackendApiError(response.getValue())
    }

    open override fun validateResponse(body: JSONItem): NetworkServiceError? {
        val parsedResponse = DiehardStatus3dsResponse.status3dsFromJsonItem(body)
        if (parsedResponse.isError()) {
            return null
        }
        val response = parsedResponse.getValue()
        if (!this.knownValidStatuses.has(response.status)) {
            return DiehardBackendApiError(response)
        }
        return null
    }

    open override fun wrapError(error: NetworkServiceError): NetworkServiceError {
        if (error is DiehardBackendApiError) {
            return error
        }
        return error.errorWithTrigger(ExternalErrorTrigger.diehard)
    }

}

