package com.yandex.pay.core.events

internal sealed class Event(
    val name: String,
    val additionalParams: Map<String, String> = emptyMap()
) {
    object Success : Event("SUCCESS")
    class Failure(message: String) : Event("FAILURE", mapOf("MESSAGE" to message))
    class Cancelled(method: ClosedEventMethod) :
        Event("CANCELLED", mapOf("METHOD" to method.toString())) {
        enum class ClosedEventMethod {
            SWIPE, TAP, BACK;

            override fun toString(): String = when (this) {
                SWIPE -> "SWIPE"
                TAP -> "TAP"
                BACK -> "BACK"
            }
        }
    }

    object PayButtonTapped : Event("PAY_BUTTON_TAPPED")
    class PayButtonUpdate(error: Throwable?) :
        Event("PAY_BUTTON_UPDATE", mapOf("SUCCESS" to (error == null).toString()))

    class PayButtonUpdateByHost(error: Throwable?) :
        Event("PAY_BUTTON_UPDATE_BY_HOST", mapOf("SUCCESS" to (error == null).toString()))

    object CheckoutButtonTapped : Event("CHECKOUT_BUTTON_TAPPED")
    object CardSelectionTapped : Event("CARD_SELECTION_TAPPED")
    class CardSelected(newCard: Boolean) :
        Event("CARD_SELECTED", mapOf("NEW_CARD" to newCard.toString()))

    object CardSelectionCancelled : Event("CARD_SELECTION_CANCELLED")
    object LicenseAgreementTapped : Event("LICENSE_AGREEMENT_TAPPED")
    object AuthorizationRequested : Event("AUTHORIZATION_REQUESTED")
    class AuthorizationHappened(result: Result) :
        Event("AUTHORIZATION_RESULT", mapOf("RESULT" to result.toString())) {
        enum class Result {
            GRANTED, DENIED, CANCELLED;

            override fun toString(): String = when (this) {
                GRANTED -> "GRANTED"
                DENIED -> "DENIED"
                CANCELLED -> "CANCELLED"
            }
        }
    }

    class NewCardBindingStarted(fromCardsList: Boolean) :
        Event("CARD_BINDING_STARTED", mapOf("FROM_CARDS_LIST" to fromCardsList.toString()))

    object NewCardBindingCancelled : Event("CARD_BINDING_CANCELLED")
    object NewCardBindingComplete : Event("CARD_BINDING_COMPLETE")
    object NewCardBindingFailed : Event("CARD_BINDING_FAILED")
    object NewCardBinding3DSRequired : Event("CARD_BINDING_3DS_REQUIRED")
}
