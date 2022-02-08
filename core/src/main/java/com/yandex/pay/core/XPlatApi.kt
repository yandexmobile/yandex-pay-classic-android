package com.yandex.pay.core

import com.yandex.pay.core.data.*
import com.yandex.pay.core.data.Merchant
import com.yandex.pay.core.data.PaymentMethod
import com.yandex.pay.core.data.PaymentSheet
import com.yandex.pay.core.data.UserCard
import com.yandex.pay.core.data.ValidationResult
import com.yandex.pay.core.utils.Runner
import com.yandex.pay.core.utils.isAuthorizationError
import com.yandex.pay.core.utils.toXPlatApiException
import com.yandex.xplat.common.DefaultJSONSerializer
import com.yandex.xplat.common.DefaultNetwork
import com.yandex.xplat.common.NetworkConfig
import com.yandex.xplat.common.map
import com.yandex.xplat.yandex.pay.*
import java.net.URL
import kotlin.Result as KResult

internal class XPlatApi(
    token: OAuthToken,
    networkConfig: NetworkConfig,
    testing: Boolean,
    private val resultsRunner: Runner,
) {
    private val factory: YandexPayApiFactory = with(DefaultJSONSerializer()) {
        YandexPayApiFactory(
            DefaultNetwork(
                if (testing) TESTING_URL else PROD_URL,
                networkConfig,
                this,
            ),
            this,
        )
    }

    private val api: YandexPayApi = factory.createForUser(token.value, null)

    fun getUserCards(completion: (KResult<List<UserCard>>) -> Unit) {
        api.userCards(UserCardsRequest())
            .then { response ->
                resultsRunner {
                    completion(KResult.success(response.cards.map(UserCard::from)))
                }
            }
            .failed { error ->
                resultsRunner {
                    completion(KResult.failure(error.toXPlatApiException()))
                }
            }
    }

    fun checkout(
        cardId: CardID,
        merchant: Merchant,
        paymentSheet: PaymentSheet,
        completion: (KResult<PaymentCheckoutResult>) -> Unit,
    ) {
        api.checkout(
            PayCheckoutRequest(
                cardId.value,
                merchant.origin,
                paymentSheet.xplat,
            )
        )
            .then { response ->
                resultsRunner {
                    completion(KResult.success(PaymentCheckoutResult.from(response)))
                }
            }
            .failed { error ->
                resultsRunner {
                    completion(KResult.failure(error.toXPlatApiException()))
                }
            }
    }

    fun setDefaultCard(cardId: CardID, completion: (KResult<Unit>) -> Unit) {
        api.setDefaultCard(SetDefaultCardRequest(cardId.value))
            .then {
                resultsRunner {
                    completion(KResult.success(Unit))
                }
            }
            .failed { error ->
                resultsRunner {
                    completion(KResult.failure(error.toXPlatApiException()))
                }
            }
    }

    fun validate(
        merchantOrigin: String,
        paymentSheet: PaymentSheet,
        completion: (KResult<ValidationResult>) -> Unit,
    ) {
        api.validate(ValidateRequest(merchantOrigin, paymentSheet.xplat))
            .then {
                resultsRunner {
                    completion(KResult.success(ValidationResult.OK))
                }
            }
            .failed { error ->
                resultsRunner {
                    when {
                        !error.isAuthorizationError() -> {
                            val validationResult =
                                (error as? APIError)?.desc?.let(ValidationResult::fromErrorCode)
                                    ?: ValidationResult.UnknownValidationProblem
                            completion(KResult.success(validationResult))
                        }
                        else -> completion(KResult.failure(error.toXPlatApiException()))
                    }
                }
            }
    }

    fun isReadyToPay(
        merchant: Merchant,
        existingPaymentMethodRequired: Boolean,
        paymentMethods: List<PaymentMethod>,
        completion: (KResult<Boolean>) -> Unit,
    ) {
        api.isReadyToPay(
            IsReadyToPayRequest(
                merchant.origin,
                merchant.id.value,
                existingPaymentMethodRequired,
                paymentMethods.map(PaymentMethod::xplat).toMutableList(),
            )
        )
            .then { response ->
                resultsRunner {
                    completion(KResult.success(response.isReadyToPay))
                }
            }
            .failed { error ->
                resultsRunner {
                    completion(KResult.failure(error.toXPlatApiException()))
                }
            }
    }

    fun loadAvatar(completion: (KResult<UserProfile.Unresolved>) -> Unit) {
        api.loadUserProfile(UserProfileRequest())
            .then { response ->
                resultsRunner {
                    completion(KResult.success(UserProfile.from(response)))
                }
            }
            .failed { error ->
                resultsRunner {
                    completion(KResult.failure(error.toXPlatApiException()))
                }
            }
    }

    private companion object {
        val TESTING_URL = URL("https://test.pay.yandex.ru/")
        val PROD_URL = URL("https://pay.yandex.ru/")
    }
}
