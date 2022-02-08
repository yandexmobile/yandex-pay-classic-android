package com.yandex.pay.core.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yandex.pay.core.infra.State

internal class AppState : State {
    val generalState: LiveData<GeneralState> get() = mutableGeneralState
    private val mutableGeneralState: MutableLiveData<GeneralState> =
        MutableLiveData(GeneralState.create())

    val navigation: LiveData<NavigationState> get() = mutableNavigation
    private val mutableNavigation: MutableLiveData<NavigationState> =
        MutableLiveData(NavigationState.create())

    val userCards: LiveData<UserCardsState> get() = mutableUserCards
    private val mutableUserCards: MutableLiveData<UserCardsState> =
        MutableLiveData(UserCardsState.create())

    val authorization: LiveData<AuthorizationState> get() = mutableAuthorization
    private val mutableAuthorization: MutableLiveData<AuthorizationState> = MutableLiveData(
        AuthorizationState.create()
    )

    val orderDetails: LiveData<OrderDetailsState> get() = mutableOrderDetailsState
    private val mutableOrderDetailsState: MutableLiveData<OrderDetailsState> =
        MutableLiveData(OrderDetailsState.create())

    val checkoutState: LiveData<CheckoutState> get() = mutableCheckoutState
    private val mutableCheckoutState: MutableLiveData<CheckoutState> =
        MutableLiveData(CheckoutState.create())

    val cardBindingState: LiveData<CardBindingState> get() = mutableCardBindingState
    private val mutableCardBindingState: MutableLiveData<CardBindingState> =
        MutableLiveData(CardBindingState.create())

    fun withGeneralState(value: GeneralState): AppState = also {
        mutableGeneralState.value = value
    }

    fun withNavigation(value: NavigationState): AppState = also {
        mutableNavigation.value = value
    }

    fun withUserCards(value: UserCardsState): AppState = also {
        mutableUserCards.value = value
    }

    fun withAuthorization(value: AuthorizationState): AppState = also {
        mutableAuthorization.value = value
    }

    fun withOrderDetails(value: OrderDetailsState): AppState = also {
        mutableOrderDetailsState.value = value
    }

    fun withCheckoutState(value: CheckoutState): AppState = also {
        mutableCheckoutState.value = value
    }

    fun withCardBindingState(value: CardBindingState): AppState = also {
        mutableCardBindingState.value = value
    }

    fun with(
        generalState: GeneralState? = null,
        navigation: NavigationState? = null,
        userCards: UserCardsState? = null,
        authorization: AuthorizationState? = null,
        orderDetails: OrderDetailsState? = null,
        checkoutState: CheckoutState? = null,
        cardBindingState: CardBindingState? = null,
    ): AppState = also {
        generalState?.let(mutableGeneralState::setValue)
        navigation?.let(mutableNavigation::setValue)
        userCards?.let(mutableUserCards::setValue)
        authorization?.let(mutableAuthorization::setValue)
        orderDetails?.let(mutableOrderDetailsState::setValue)
        checkoutState?.let(mutableCheckoutState::setValue)
        cardBindingState?.let(mutableCardBindingState::setValue)
    }
}
