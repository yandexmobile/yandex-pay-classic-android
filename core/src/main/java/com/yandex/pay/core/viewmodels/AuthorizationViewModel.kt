package com.yandex.pay.core.viewmodels

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.yandex.pay.core.actions.AuthorizationAction
import com.yandex.pay.core.actions.NavigationAction
import com.yandex.pay.core.auth.Authorizer
import com.yandex.pay.core.data.Error
import com.yandex.pay.core.data.OAuthToken
import com.yandex.pay.core.data.ValidationResult
import com.yandex.pay.core.events.Event
import com.yandex.pay.core.infra.Store
import com.yandex.pay.core.state.AppState
import com.yandex.pay.core.utils.StateRestoration

internal class AuthorizationViewModel(
    application: Application,
    fragment: Fragment,
    private val parentViewModel: MainViewModel,
) : BaseViewModel(application) {
    private val store: Store
        get() = parentViewModel.store
    private val state: AppState
        get() = store.state

    val loading: LiveData<Boolean> = state.authorization.map { it.loading }.distinctUntilChanged()

    // `loading` becomes true way earlier than we start the authorizer.
    // Checking it will prevent `loadToken` from work, so we introduce a flag that's toggled right on time.
    private var authInProgress: Boolean = false

    private val tokenPresent: Boolean
        get() = state.authorization.value?.token?.isEmpty != true

    private val authorizer: Authorizer = Authorizer(
        fragment,
        components.config.logging,
        useTestEnvironment = false,
        components.authTokenStorage,
        ::processAuthResult,
    )

    fun loadToken() {
        if (authInProgress || tokenPresent) {
            return
        }

        authInProgress = true
        if (authorizer.authorize(applicationContext)) {
            logEvent(Event.AuthorizationRequested)
        }
    }

    fun save(bundle: Bundle) {
        StateRestoration.saveAuthorizationData(bundle, authInProgress)
    }

    fun restore(bundle: Bundle) {
        authInProgress = StateRestoration.loadAuthorizationData(bundle) == true
    }

    private fun processAuthResult(payload: Authorizer.Payload) {
        authInProgress = false
        if (payload.fromSDK) {
            logAuthResult(payload.result)
        }
        payload.result
            .onSuccess { token ->
                if (token != null) {
                    store.dispatch(AuthorizationAction.StoreToken(token))
                    store.dispatch(AuthorizationAction.Complete)
                } else {
                    store.dispatch(AuthorizationAction.Cancel)
                }
            }
            .onFailure {
                store.dispatch(NavigationAction.CompleteWithError(Error(ValidationResult.AuthenticationProblem)))
            }
    }

    private fun logAuthResult(result: Result<OAuthToken?>) {
        val outcome = result.fold(
            { if (it != null) Event.AuthorizationHappened.Result.GRANTED else Event.AuthorizationHappened.Result.CANCELLED },
            { Event.AuthorizationHappened.Result.DENIED }
        )
        logEvent(Event.AuthorizationHappened(outcome))
    }

    class Factory(
        private val application: Application,
        private val fragment: Fragment,
        private val parentViewModel: MainViewModel
    ) :
        ViewModelProvider.AndroidViewModelFactory(application) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            takeIf { modelClass == AuthorizationViewModel::class.java }?.let {
                AuthorizationViewModel(application, fragment, parentViewModel)
            } as T
    }
}
