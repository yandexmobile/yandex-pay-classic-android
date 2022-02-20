package com.yandex.pay.core.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.Vibrator
import androidx.core.content.getSystemService
import androidx.lifecycle.*
import com.yandex.pay.core.R
import com.yandex.pay.core.YandexPayLib
import com.yandex.pay.core.actions.CheckoutActions
import com.yandex.pay.core.actions.GeneralActions
import com.yandex.pay.core.actions.NavigationAction
import com.yandex.pay.core.actions.UserCardsAction
import com.yandex.pay.core.data.*
import com.yandex.pay.core.events.Event
import com.yandex.pay.core.infra.Store
import com.yandex.pay.core.navigation.Route
import com.yandex.pay.core.state.AppState
import com.yandex.pay.core.state.CheckoutState
import com.yandex.pay.core.state.GeneralState
import com.yandex.pay.core.state.UserCardsState
import com.yandex.pay.core.ui.views.interfaces.*
import com.yandex.pay.core.ui.views.presenters.*
import com.yandex.pay.core.userprofile.UserProfileDataSource
import com.yandex.pay.core.userprofile.UserProfileLoader
import com.yandex.pay.core.utils.currentLocale


internal class PaymentViewModel(
    application: Application,
    private val parentViewModel: MainViewModel,
) : BaseViewModel(application) {
    private val store: Store get() = parentViewModel.store
    private val state: AppState get() = store.state
    private val userCards: LiveData<UserCardsState> get() = state.userCards

    private val cardPresenter =
        CardItemPresenter(applicationContext.resources, ICardItemView.AccessoryType.Disclosure)
    private val checkoutButtonPresenter = CheckoutButtonPresenter(
        orderDetails.currencyCode,
        applicationContext.resources.configuration.currentLocale
    )
    private val licenseAgreementPresenter: LicenseAgreementPresenter =
        LicenseAgreementPresenter(
            applicationContext.resources,
            applicationContext.theme,
            "license_agreement_link"
        )
    private val headerPresenter: HeaderPresenter = HeaderPresenter()
    private val avatarPresenter: AvatarPresenter = AvatarPresenter()
    private val userProfileDataSource: UserProfileDataSource = UserProfileDataSource(
        UserProfileLoader(application.applicationContext, components),
    )

    val operationsAvailability: Boolean
        get() = !(loadingValue || checkoutInProgressValue || hasError)

    private val userCardsList: List<UserCard>? get() = userCards.value!!.cards

    private val userCardsNotEmptyValue: Boolean
        get() = userCardsList?.isNotEmpty() == true

    private val loadingValue: Boolean get() = parentViewModel.loadingValue

    private val validatedValue: Boolean
        get() = with(state.orderDetails.value!!) { !validating && orderDetails != null }

    private val orderDetails: OrderDetails
        get() = parentViewModel.requireOrderDetails

    private val checkoutInProgressValue: Boolean
        get() = state.checkoutState.value?.checkingOut == true
    private val checkoutResultValue: PaymentCheckoutResult?
        get() = state.checkoutState.value?.checkoutResult

    private val error: LiveData<ErrorType?> =
        state.generalState.map(GeneralState::error).distinctUntilChanged()
    private val errorValue: ErrorType? get() = error.value
    private val hasError: Boolean
        get() = state.generalState.value!!.error != null

    private val mutableUserProfile: MutableLiveData<AvatarPresenter.Payload> =
        MutableLiveData(AvatarPresenter.Payload.Loading)
    val userProfile: LiveData<AvatarPresenter.Payload>
        get() = mutableUserProfile

    internal sealed interface UIState {
        data class Normal(val selectedCardIndex: Int) : UIState
        object NoCards : UIState
        object Loading : UIState
        object CheckingOut : UIState
        data class Error(val error: ErrorType?) : UIState
        object Done : UIState
    }

    val uiState: LiveData<UIState> = MediatorLiveData<UIState>().apply {
        fun foldState(
            loading: Boolean = loadingValue,
            checkingOut: Boolean = checkoutInProgressValue,
            clearError: Boolean = false,
            error: ErrorType? = errorValue,
            checkedOut: Boolean = checkoutResultValue != null,
            cards: List<*>? = userCardsList,
            selectedCardIndex: Int = userCards.value!!.selected,
        ): UIState = when {
            error != null && !clearError -> UIState.Error(error)
            error == null && clearError -> UIState.Error(null)
            checkedOut -> UIState.Done
            loading -> UIState.Loading
            checkingOut -> UIState.CheckingOut
            cards == null -> UIState.Loading
            cards.isEmpty() -> UIState.NoCards
            else -> UIState.Normal(selectedCardIndex)
        }

        addSource(parentViewModel.loading.distinctUntilChanged()) {
            value = foldState(loading = it)
        }
        addSource(
            state.checkoutState.map(CheckoutState::checkingOut)
                .distinctUntilChanged()
        ) { value = foldState(checkingOut = it) }
        addSource(error) { value = foldState(clearError = it == null, error = it) }
        addSource(
            state.checkoutState.map(CheckoutState::checkedOut).distinctUntilChanged()
        ) { value = foldState(checkedOut = it) }
        addSource(userCards.map(UserCardsState::cards)) { value = foldState(cards = it) }
        addSource(userCards.map(UserCardsState::selected).distinctUntilChanged()) {
            value = foldState(selectedCardIndex = it)
        }

        value = UIState.Loading
    }

    fun bindView(headerView: IHeaderView) {
        headerPresenter.present(HeaderPresenter.Payload.Root, headerView)
    }

    fun bindView(avatarView: IAvatarView, avatarTapped: () -> Unit) {
        val payload = userProfile.value?.let {
            when (it) {
                AvatarPresenter.Payload.Loading -> it
                is AvatarPresenter.Payload.Data -> AvatarPresenter.Payload.Data(
                    it.profile,
                    operationsAvailability,
                    avatarTapped
                )
            }
        } ?: return
        avatarPresenter.present(payload, avatarView)
    }

    fun bindView(cardView: ICardItemView) {
        val payload = when {
            userCardsNotEmptyValue -> {
                val currentCard = selectedCard ?: return
                CardItemPresenter.Payload.Card(currentCard, true) { routeToCardSelection() }
            }
            loadingValue -> CardItemPresenter.Payload.Loading
            else -> CardItemPresenter.Payload.AddItem { routeToNewCardBinding() }
        }
        cardPresenter.present(payload, cardView)
    }

    fun bindView(checkoutButton: ICheckoutButtonView) {
        val error = error.value
        val payload = when {
            error != null -> CheckoutButtonPresenter.Payload.Error.also {
                val block = when (error) {
                    is ErrorType.Recoverable -> Runnable { store.dispatch(GeneralActions.RecoverFromError) }
                    is ErrorType.Fatal -> Runnable { parentViewModel.finishWithError(error.error) }
                }
                parentViewModel.runPostponed(
                    resources.getInteger(R.integer.yandexpay_short_error_hide_timeout).toLong(),
                    block
                )
            }
            checkoutResultValue != null -> CheckoutButtonPresenter.Payload.CheckedOut.also {
                playFeedback()
            }
            loadingValue -> CheckoutButtonPresenter.Payload.Loading
            checkoutInProgressValue -> CheckoutButtonPresenter.Payload.CheckoutInProgress
            else -> CheckoutButtonPresenter.Payload.Normal(
                orderDetails.order.amount,
                !userCardsNotEmptyValue,
                ::checkout
            )
        }
        checkoutButtonPresenter.present(payload, checkoutButton)
    }

    fun bindView(licenseAgreementView: ILicenseAgreementView, activity: Activity) {
        val payload = LicenseAgreementPresenter.Payload(R.string.yandexpay_license_agreement_text) {
            logEvent(Event.LicenseAgreementTapped)
            val url = Uri.parse(activity.getString(R.string.yandexpay_license_agreement_url))
            val intent = Intent(Intent.ACTION_VIEW, url)
            activity.startActivity(intent)
        }
        licenseAgreementPresenter.present(payload, licenseAgreementView)
    }

    private val selectedCard: UserCard?
        get() {
            val (cards, selected, _) = userCards.value!!
            return cards?.getOrNull(selected)
        }

    fun loadUserCards() {
        if (loadingValue || userCardsList != null) {
            // Data is obtained, or being obtained.
            return
        }
        val action = UserCardsAction.GetCards.create(
            !validatedValue,
            orderDetails,
            components.payApi,
            store::dispatch,
            ::processErrorResponse
        )
        store.dispatch(action)
    }

    fun initiateCompletion() {
        parentViewModel.initiateCompletion(checkoutResultValue)
    }

    fun loadUserProfile() {
        userProfileDataSource.fetch { payload ->
            if (payload is AvatarPresenter.Payload.Data) {
                YandexPayLib.instance.avatar.postValue(payload.profile.imageOrNull)
            }
            mutableUserProfile.postValue(payload)
        }
    }

    private fun checkout() {
        logEvent(Event.CheckoutButtonTapped)

        val selectedCard = this.selectedCard ?: return
        val checkoutData = CheckoutData(
            selectedCard.id,
            orderDetails.merchant,
            orderDetails.paymentSheet,
        )
        val action = CheckoutActions.Checkout.create(
            checkoutData,
            components.payApi,
            store::dispatch,
            ::processErrorResponse
        )
        store.dispatch(action)
    }

    private fun processErrorResponse(error: Throwable) {
        processError(error, parentViewModel)
    }

    private fun routeToCardSelection() {
        logEvent(Event.CardSelectionTapped)
        store.dispatch(NavigationAction.Push(Route.SelectCard))
    }

    private fun routeToNewCardBinding() {
        parentViewModel.startNewCardBinding(false)
    }

    private fun playFeedback() {
        @Suppress("DEPRECATION")
        with(applicationContext) {
            getSystemService<Vibrator>()
                ?.vibrate(resources.getInteger(R.integer.yandexpay_vibro_duration).toLong())
        }
    }

    fun runAfterError() {
        if (!validatedValue || userCardsList == null) {
            loadUserCards()
            loadUserProfile()
        }
    }

    fun logout() {
        parentViewModel.reauthorize()
    }

    class Factory(
        private val application: Application,
        private val parentViewModel: MainViewModel
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            takeIf { modelClass == PaymentViewModel::class.java }?.let {
                PaymentViewModel(application, parentViewModel)
            } as T
    }
}
