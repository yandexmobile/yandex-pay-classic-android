package com.yandex.pay.core.cardbinding

import android.content.res.Resources
import androidx.annotation.StringRes
import com.yandex.pay.core.R
import com.yandex.pay.core.data.CardID
import com.yandex.pay.core.data.OAuthToken
import com.yandex.pay.core.data.ServiceToken
import com.yandex.pay.core.data.Uid
import com.yandex.pay.core.utils.AuthorizationException
import com.yandex.pay.core.utils.isAuthorizationError
import com.yandex.xplat.common.*
import com.yandex.xplat.yandex.pay.*
import java.net.URL
import kotlin.Result as KResult

internal class DiehardApi(
    token: OAuthToken,
    uid: Uid,
    resources: Resources,
    networkConfig: NetworkConfig,
    testing: Boolean,
) {
    private val serviceToken: ServiceToken =
        getServiceToken(
            resources,
            testing,
            R.string.yandexpay_diehard_stage_service_token,
            R.string.yandexpay_diehard_prod_service_token
        )

    private val jsonSerializer: JSONSerializer = DefaultJSONSerializer()
    private val diehardNetwork: Network = DefaultNetwork(
        getServiceURL(
            resources,
            testing,
            R.string.yandexpay_diehard_stage_url_string,
            R.string.yandexpay_diehard_prod_url_string
        ),
        networkConfig,
        jsonSerializer,
    )
    private val paymentSdkNetwork: Network = DefaultNetwork(
        getServiceURL(
            resources,
            testing,
            R.string.yandexpay_mobileapi_stage_url_string,
            R.string.yandexpay_mobileapi_prod_url_string
        ),
        networkConfig,
        jsonSerializer,
    )
    private val diehardApi: DiehardBackendApi = DiehardBackendApi.create(
        diehardNetwork,
        jsonSerializer,
        token.value,
    )
    private val mobileApi: MobileBackendApi = MobileBackendApi.create(
        paymentSdkNetwork,
        jsonSerializer,
        serviceToken.value,
        { toPromise(MobileBackendAuthorization.fromAuthorizationPair(token.value, uid.value)) },
        ClientPlatform.android,
        resources.getString(R.string.yandexpay_mobileapi_version),
        resources.getBoolean(R.bool.yandexpay_mobileapi_force_cvv),
        token.value,
    )

    private val cardBindingService: CardBindingService = CardBindingService(
        Payer(token.value, null, null),
        serviceToken.value,
        jsonSerializer,
        DefaultCardDataCipher(
            if (testing) R.raw.yandexpay_bindings_key_debug else R.raw.yandexpay_bindings_key_release,
            resources
        ),
        mobileApi,
        diehardApi,
        resources.getInteger(R.integer.yandexpay_diehard_region_id),
    )

    private fun getServiceURL(
        resources: Resources,
        testing: Boolean,
        @StringRes staging: Int,
        @StringRes prod: Int
    ): URL =
        URL(resources.getString(if (testing) staging else prod))

    private fun getServiceToken(
        resources: Resources,
        testing: Boolean,
        @StringRes staging: Int,
        @StringRes prod: Int,
    ): ServiceToken = ServiceToken(resources.getString(if (testing) staging else prod))

    fun bind(
        card: NewCard,
        challengeCallback: ChallengeCallback,
        completion: (KResult<CardID?>) -> Unit
    ) {
        cardBindingService.bind(card, object : ChallengeCallback {
            override fun show3ds(uri: Uri) {
                if (!isCancelled) {
                    challengeCallback.show3ds(uri)
                }
            }

            override fun hide3ds(success: Boolean) {
                if (!isCancelled) {
                    challengeCallback.hide3ds(success)
                }
            }
        }).then {
            completion(KResult.success(CardID.from(it.cardId)))
        }.failed { error ->
            completion(
                if (error is CardBindingServiceError && error.code == CardBindingServiceErrorCode.cancelled)
                    KResult.success(null)
                else if (error.isAuthorizationError()) {
                    KResult.failure(AuthorizationException("Unauthorized"))
                } else {
                    KResult.failure(error)
                }
            )
        }
    }

    fun cancel() {
        cardBindingService.cancel()
    }

    val isCancelled: Boolean get() = cardBindingService.isCancelled()
}
