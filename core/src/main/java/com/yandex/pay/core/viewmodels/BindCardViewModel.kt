package com.yandex.pay.core.viewmodels

import android.app.Application
import android.os.Bundle
import androidx.annotation.IntRange
import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.yandex.pay.core.R
import com.yandex.pay.core.actions.GeneralActions
import com.yandex.pay.core.actions.UserCardsAction
import com.yandex.pay.core.data.CardDetails
import com.yandex.pay.core.data.ErrorType
import com.yandex.pay.core.events.Event
import com.yandex.pay.core.infra.Store
import com.yandex.pay.core.ui.views.interfaces.*
import com.yandex.pay.core.ui.views.presenters.*
import com.yandex.pay.core.utils.CardDetailsFormatter
import com.yandex.pay.core.utils.StateRestoration
import com.yandex.xplat.yandex.pay.*

internal class BindCardViewModel(
    application: Application,
    private val parentViewModel: MainViewModel
) : BaseViewModel(application) {
    enum class State {
        CARD_NUMBER, CARD_DETAILS, BINDING, DONE,
    }

    private val store: Store
        get() = parentViewModel.store

    private val bindingInProgress: Boolean
        get() = with(store.state.cardBindingState.value) { this?.cardDetails != null || this?.show3DS != null }

    private val headerPresenter: HeaderPresenter = HeaderPresenter()
    private val bindCardButtonPresenter: BindCardButtonPresenter = BindCardButtonPresenter()

    private val cardNumberInputController: CardNumberInputController = CardNumberInputController(
        DefaultCardNumberValidator(),
        CardDetailsFormatter(CardDetailsFormatter.Context.CARD_NUMBER_COLLAPSED_INPUT),
        application.resources,
    )

    private val cvnInputController: CvnInputController = CvnInputController(
        DefaultCardCvnValidator(),
    )

    private val expirationDateInputController: ExpirationDateInputController =
        ExpirationDateInputController(
            DefaultCardExpirationDateValidator()
        )

    private val errorTextViewPresenter: ErrorTextViewPresenter = ErrorTextViewPresenter()

    private val mutableBindCardButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val bindCardButtonEnabled: LiveData<Boolean> get() = mutableBindCardButtonEnabled

    private val mutableLocalServiceError: MutableLiveData<String?> = MutableLiveData(null)
    private val mutableValidationError: MutableLiveData<String?> = MutableLiveData(null)

    val error: LiveData<String?> = MediatorLiveData<String?>().apply {
        val generalErrorSource = store.state.generalState.map { state ->
            when (val err = state.error) {
                null -> null
                is ErrorType.Fatal -> err.error.code.descriptionResourceID?.let(resources::getString)
                is ErrorType.Recoverable -> resources.getString(R.string.yandexpay_network_error)
            }
        }

        fun setValue(signal: String?, @IntRange(from = 1, to = 3) source: Int) {
            value = when (source) {
                1 -> signal ?: mutableLocalServiceError.value ?: mutableValidationError.value
                2 -> signal ?: generalErrorSource.value ?: mutableValidationError.value
                3 -> signal ?: generalErrorSource.value ?: mutableLocalServiceError.value
                else -> null
            }
        }

        addSource(generalErrorSource) { setValue(it, 1) }
        addSource(mutableLocalServiceError) { setValue(it, 2) }
        addSource(mutableValidationError) { setValue(it, 3) }

        value = null
    }

    val recoverableError: LiveData<ErrorType.Recoverable?> = store.state.generalState.map { state ->
        state.error?.let { it as ErrorType.Recoverable }
    }

    val hasExpirationDate: Boolean
        get() = expirationDateInputController.hasExpirationDate

    val isExpirationDateValid: Boolean
        get() = expirationDateInputController.isValid

    val hasCvn: Boolean
        get() = cvnInputController.hasCvn

    val isCvnValid: Boolean
        get() = cvnInputController.isValid

    private var details: CardDetails = CardDetails.empty

    private val mutableCurrentState: MutableLiveData<State> = MutableLiveData(State.CARD_NUMBER)
    val currentState: LiveData<State>
        get() = mutableCurrentState

    fun onBackPressed(): Boolean {
        when (currentState.value!!) {
            State.CARD_NUMBER -> parentViewModel.goBack()
            State.CARD_DETAILS -> moveToCardNumberInput()
            State.BINDING -> cancelBinding()
            State.DONE -> parentViewModel.goToInitial()
        }
        return true
    }

    fun restore(bundle: Bundle?) {
        if (bundle != null) {
            // Restoring after rotation most likely
            val result = (StateRestoration.loadCardBindingState(bundle) ?: State.CARD_NUMBER)
                .takeUnless { it == State.BINDING && !bindingInProgress }
                ?: State.CARD_DETAILS
            mutableCurrentState.value = result
            details = StateRestoration.loadCardDetails(bundle) ?: CardDetails.empty
        } else {
            // Getting back from 3DS
            mutableCurrentState.value =
                if (details.isEmpty) State.CARD_NUMBER else State.CARD_DETAILS
        }
    }

    fun save(bundle: Bundle) {
        StateRestoration.saveCardBindingState(bundle, currentState.value!!)
        StateRestoration.saveCardDetails(bundle, details)
    }

    fun moveToOtherCardDetailsInput() {
        mutableCurrentState.postValue(State.CARD_DETAILS)
    }

    fun moveToCardNumberInput() {
        mutableCurrentState.postValue(State.CARD_NUMBER)
    }

    val cardNumberExpanded: Boolean
        get() = cardNumberInputController.state == ICardNumberInput.TextState.FULL

    fun expandCardNumberInput() {
        cardNumberInputController.state = ICardNumberInput.TextState.FULL
    }

    fun collapseCardNumberInput() {
        cardNumberInputController.state = ICardNumberInput.TextState.MASKED
    }

    fun bind(cardNumberInput: ICardNumberInput) {
        val payload = if (details.isPartiallyFilled) {
            CardNumberInputController.Payload.Restore(details.number, ::onCardNumberInputDone)
        } else {
            CardNumberInputController.Payload.Pristine(::onCardNumberInputDone)
        }
        cardNumberInputController.bind(payload, cardNumberInput)
        cardNumberInput.onError = ::signalValidationError
    }

    private fun onCardNumberInputDone(cardNumber: String) {
        updateCardNumber(cardNumber)
        moveToOtherCardDetailsInput()
    }

    fun onCardNumberValueChanged() {
        updateBindCardButtonState()
    }

    fun onExpirationDateValueChanged() {
        updateBindCardButtonState()
    }

    fun onCvnValueChanged() {
        updateBindCardButtonState()
    }

    fun updateBindCardButtonState() {
        mutableBindCardButtonEnabled.value = when (currentState.value!!) {
            State.CARD_NUMBER -> cardNumberInputController.isValid
            State.CARD_DETAILS -> isEditedFieldValid || isNoneFocusedAndValid
            State.DONE, State.BINDING -> false
        }
    }

    fun bind(expirationDateInput: IExpirationDateInput) {
        val payload = ExpirationDateInputController.Payload(
            ExpirationDateInputController.Date.from(details),
            ::updateExpirationDate,
        )
        expirationDateInputController.bind(payload, expirationDateInput)
        expirationDateInput.onError = ::signalValidationError
    }

    fun bind(cvnInput: ICvnInput) {
        val payload = CvnInputController.Payload(::updateCvn)
        cvnInputController.bind(payload, cvnInput)
        cvnInput.onError = ::signalValidationError
    }

    fun bind(header: IHeaderView) {
        headerPresenter.present(
            HeaderPresenter.Payload.BackButton(::onBackPressed),
            header
        )
    }

    fun bind(errorTextView: IErrorTextView) {
        errorTextViewPresenter.present(ErrorTextViewPresenter.Payload(error.value), errorTextView)
    }

    fun unbind(cardNumberInput: ICardNumberInput) {
        cardNumberInput.onError = null
        cardNumberInputController.unbind(cardNumberInput)
    }

    fun unbind(expirationDateInput: IExpirationDateInput) {
        expirationDateInput.onError = null
        expirationDateInputController.unbind(expirationDateInput)
    }

    fun unbind(cvnInput: ICvnInput) {
        cvnInput.onError = null
        cvnInputController.unbind(cvnInput)
    }

    fun validateCardNumberInput(): CardValidationError? = cardNumberInputController.validate()

    fun validateExpirationDateInput(): CardValidationError? =
        expirationDateInputController.validate()

    fun validateCvnInput(): CardValidationError? = cvnInputController.validate()

    fun completeCardDataEntry() {
        mutableCurrentState.value = State.BINDING
        store.dispatch(
            UserCardsAction.BindCard.create(
                details.toNewCard(),
                components.diehardApi,
                store::dispatch,
                components.mainThreadRunner,
                ::doneBinding,
            )
        )
    }

    fun bind(bindCardButton: IBindCardButtonView, onClick: () -> Unit) {
        val payload = when {
            mutableLocalServiceError.value != null || store.state.generalState.value?.error != null -> BindCardButtonPresenter.Payload.Error(
                resources.getString(R.string.yandexpay_checkout_error_title)
            )
            currentState.value == State.BINDING -> BindCardButtonPresenter.Payload.Progress(
                resources.getString(R.string.yandexpay_bind_card_progress_title)
            )
            currentState.value == State.DONE -> BindCardButtonPresenter.Payload.Done(
                resources.getString(
                    R.string.yandexpay_bind_card_done_title
                )
            )
            currentState.value == State.CARD_NUMBER || currentState.value == State.CARD_DETAILS -> BindCardButtonPresenter.Payload.Normal(
                resources.getString(
                    if (hasCvn && hasExpirationDate && currentState.value == State.CARD_DETAILS) R.string.yandexpay_next_button_complete_title
                    else R.string.yandexpay_next_button_incomplete_title
                ),
                bindCardButtonEnabled.value!!,
                onClick,
            )
            else -> null
        }
        if (payload != null) {
            bindCardButtonPresenter.present(payload, bindCardButton)
        }
    }

    fun setupLocalServiceErrorCleanup() {
        parentViewModel.runPostponed(
            resources.getInteger(R.integer.yandexpay_long_error_hide_timeout).toLong()
        ) {
            mutableLocalServiceError.value = null
        }
    }

    fun setupGlobalRecoverableErrorCleanup() {
        parentViewModel.runPostponed(
            resources.getInteger(R.integer.yandexpay_long_error_hide_timeout).toLong()
        ) {
            store.dispatch(GeneralActions.RecoverFromError)
        }
    }

    private fun signalLocalServiceError(@StringRes message: Int) {
        mutableLocalServiceError.value = resources.getString(message)
        setupLocalServiceErrorCleanup()
    }

    private fun updateExpirationDate(date: ExpirationDateInputController.Date) {
        details = details.copy(expirationMonth = date.month, expirationYear = date.year)
    }

    private fun updateCvn(cvn: String) {
        details = details.copy(cvn = cvn)
    }

    private fun updateCardNumber(cardNumber: String) {
        details = details.copy(number = cardNumber)
    }

    private fun cancelBinding() {
        store.dispatch(UserCardsAction.CancelBinding.create(components.diehardApi))
        mutableCurrentState.value = State.CARD_DETAILS
    }

    private fun doneBinding(error: Throwable?) {
        if (error == null) {
            mutableCurrentState.value = State.DONE
        } else {
            processErrorResponse(error)
            mutableCurrentState.value = State.CARD_DETAILS
        }
    }

    private fun signalValidationError(error: String?) {
        mutableValidationError.value = error
    }

    private fun processErrorResponse(error: Throwable) {
        if (error is NetworkServiceError) {
            processError(error)
        } else {
            processError(error, parentViewModel)
        }
    }

    private fun processError(error: NetworkServiceError) {
        when (error.trigger) {
            ExternalErrorTrigger.diehard -> processDiehardError(error)
            ExternalErrorTrigger.mobile_backend -> processMobileApiError(error)
            else -> processUnknownError(error)
        }
    }

    private fun processDiehardError(error: NetworkServiceError) = processCardBindingError(error)

    private fun processMobileApiError(error: NetworkServiceError) = processCardBindingError(error)

    private fun processCardBindingError(error: NetworkServiceError) {
        when (error.kind) {
            ExternalErrorKind.network -> signalLocalServiceError(R.string.yandexpay_network_error)
            ExternalErrorKind.fail_3ds -> {
                logEvent(Event.NewCardBindingFailed)
                signalLocalServiceError(R.string.yandexpay_3ds_failed_error)
            }
            ExternalErrorKind.expired_card -> {
                logEvent(Event.NewCardBindingFailed)
                signalLocalServiceError(R.string.yandexpay_expired_card_error)
            }
            ExternalErrorKind.restricted_card -> {
                logEvent(Event.NewCardBindingFailed)
                signalLocalServiceError(R.string.yandexpay_restricted_card_error)
            }
            ExternalErrorKind.payment_cancelled, ExternalErrorKind.user_cancelled -> {
                logEvent(Event.NewCardBindingFailed)
                signalLocalServiceError(R.string.yandexpay_binding_cancelled_error)
            }
            ExternalErrorKind.authorization, ExternalErrorKind.payment_authorization_reject -> {
                logEvent(Event.NewCardBindingFailed)
                signalLocalServiceError(R.string.yandexpay_authorization_reject_error)
            }
            ExternalErrorKind.limit_exceeded -> {
                logEvent(Event.NewCardBindingFailed)
                signalLocalServiceError(R.string.yandexpay_limit_exceeded_error)
            }
            ExternalErrorKind.not_enough_funds -> {
                logEvent(Event.NewCardBindingFailed)
                signalLocalServiceError(R.string.yandexpay_not_enough_funds_error)
            }
            ExternalErrorKind.payment_timeout -> {
                logEvent(Event.NewCardBindingFailed)
                signalLocalServiceError(R.string.yandexpay_payment_timeout_error)
            }
            ExternalErrorKind.payment_gateway_technical_error -> {
                logEvent(Event.NewCardBindingFailed)
                signalLocalServiceError(R.string.yandexpay_technical_error)
            }
            ExternalErrorKind.invalid_processing_request -> {
                logEvent(Event.NewCardBindingFailed)
                signalLocalServiceError(R.string.yandexpay_invalid_processing_request_error)
            }
            ExternalErrorKind.transaction_not_permitted -> {
                logEvent(Event.NewCardBindingFailed)
                signalLocalServiceError(R.string.yandexpay_transaction_not_permitted_error)
            }
            else -> signalLocalServiceError(R.string.yandexpay_unknown_validation_problem)
        }
    }

    private fun processUnknownError(error: NetworkServiceError) {
        signalLocalServiceError(R.string.yandexpay_unknown_validation_problem)
    }

    private val isEditedFieldValid: Boolean
        get() = expirationDateInputController.hasFocus && expirationDateInputController.hasExpirationDate ||
            cvnInputController.hasFocus && cvnInputController.hasCvn

    private val isNoneFocusedAndValid: Boolean
        get() = !expirationDateInputController.hasFocus && !cvnInputController.hasFocus
            && expirationDateInputController.hasExpirationDate && cvnInputController.hasCvn

    internal class Factory(
        private val application: Application,
        private val parentViewModel: MainViewModel,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            takeIf { modelClass == BindCardViewModel::class.java }?.let {
                BindCardViewModel(application, parentViewModel)
            } as T
    }
}
