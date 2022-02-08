package com.yandex.pay.core.actions

import android.net.Uri
import com.yandex.pay.core.XPlatApi
import com.yandex.pay.core.cardbinding.DiehardApi
import com.yandex.pay.core.data.CardID
import com.yandex.pay.core.data.OrderDetails
import com.yandex.pay.core.data.UserCard
import com.yandex.pay.core.data.ValidationResult
import com.yandex.pay.core.infra.Dispatch
import com.yandex.pay.core.utils.Runner
import com.yandex.pay.core.utils.ValidationException
import com.yandex.xplat.yandex.pay.ChallengeCallback
import com.yandex.xplat.yandex.pay.NewCard
import com.yandex.xplat.common.Uri as XUri

internal sealed interface UserCardsAction : Action {
    class GetCards private constructor(val withValidation: Boolean) : UserCardsAction {
        internal companion object {
            fun create(
                withValidation: Boolean,
                orderDetails: OrderDetails,
                api: XPlatApi,
                dispatch: Dispatch,
                errorProcessor: ((Throwable) -> Unit)?,
            ): GetCards {
                if (withValidation) {
                    validate(orderDetails, api, dispatch, errorProcessor)
                } else {
                    loadCards(api, dispatch, errorProcessor)
                }
                return GetCards(withValidation)
            }

            private fun validate(
                orderDetails: OrderDetails,
                api: XPlatApi,
                dispatch: Dispatch,
                errorProcessor: ((Throwable) -> Unit)?,
            ) {
                api.validate(
                    orderDetails.merchant.origin,
                    orderDetails.paymentSheet
                ) { result ->
                    result
                        .onSuccess { validationResult ->
                            if (validationResult == ValidationResult.OK) {
                                dispatch(GeneralActions.ConfirmOrderDetailsValidated(orderDetails))
                                loadCards(api, dispatch, errorProcessor)
                            } else {
                                errorProcessor?.invoke(ValidationException(validationResult))
                            }
                        }
                        .onFailure {
                            errorProcessor?.invoke(it)
                        }
                }
            }

            private fun loadCards(
                api: XPlatApi,
                dispatch: Dispatch,
                errorProcessor: ((Throwable) -> Unit)?,
            ) {
                api.getUserCards { result ->
                    result
                        .onSuccess { dispatch(CardsLoaded(it)) }
                        .onFailure { error -> errorProcessor?.invoke(error) }
                }
            }
        }
    }

    class CardsLoaded(val items: List<UserCard>) : UserCardsAction
    class CardsLoadedWithSelection(val items: List<UserCard>, val selected: Int) : UserCardsAction
    class SetDefault(val cardId: CardID) : UserCardsAction

    class StartNewCardBinding(val fromCardsList: Boolean) : UserCardsAction
    class BindCard private constructor(val card: NewCard) : UserCardsAction {
        internal companion object {
            fun create(
                card: NewCard,
                api: DiehardApi,
                dispatch: Dispatch,
                runner: Runner,
                completion: (Throwable?) -> Unit,
                onShow3DS: (() -> Unit)? = null,
            ): BindCard {
                var wasShown = false
                var close3DS: ((Throwable?) -> Unit)?
                close3DS = { error: Throwable? ->
                    close3DS = null
                    dispatch(Hide3DS(error == null, wasShown))
                    completion(error)
                }
                api.bind(card, object : ChallengeCallback {
                    override fun show3ds(uri: XUri) {
                        runner {
                            wasShown = true
                            dispatch(Require3DS(Uri.parse(uri.getAbsoluteString())))
                            onShow3DS?.invoke()
                        }
                    }

                    override fun hide3ds(success: Boolean) {
                        runner { close3DS?.invoke(if (success) null else Exception()) }
                    }
                }) { result ->
                    runner {
                        result
                            .onSuccess { cardID ->
                                if (cardID != null) {
                                    onCardRegistered(cardID, card.cardNumber, dispatch)
                                    close3DS?.invoke(null)
                                }
                            }
                            .onFailure { error ->
                                dispatch(FailCardBinding)
                                close3DS?.invoke(error)
                            }
                    }
                }
                return BindCard(card)
            }

            private fun onCardRegistered(cardID: CardID, cardNumber: String, dispatch: Dispatch) {
                dispatch(CompleteCardBinding(UserCard.from(cardID, cardNumber)))
                dispatch(SetDefault(cardID))
            }
        }
    }

    class Require3DS(val uri: Uri) : UserCardsAction
    class Hide3DS(val success: Boolean, val wasShown: Boolean) : UserCardsAction
    class CompleteCardBinding(val card: UserCard) : UserCardsAction
    object FailCardBinding : UserCardsAction

    @Suppress("CanSealedSubClassBeObject")
    class CancelBinding private constructor() : UserCardsAction {
        internal companion object {
            fun create(api: DiehardApi): CancelBinding = CancelBinding().also {
                api.cancel()
            }
        }
    }
}
