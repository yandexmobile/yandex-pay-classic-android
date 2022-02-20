package com.yandex.pay.core.viewmodels

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import com.yandex.pay.core.R
import com.yandex.pay.core.actions.AuthorizationAction
import com.yandex.pay.core.actions.GeneralActions
import com.yandex.pay.core.actions.NavigationAction
import com.yandex.pay.core.actions.UserCardsAction
import com.yandex.pay.core.data.Error
import com.yandex.pay.core.data.OrderDetails
import com.yandex.pay.core.data.PaymentCheckoutResult
import com.yandex.pay.core.infra.Store
import com.yandex.pay.core.navigation.Route
import com.yandex.pay.core.navigation.RoutePresenter
import com.yandex.pay.core.utils.StateRestoration

internal class MainViewModel(
    application: Application,
    val store: Store,
    private val postponeRunner: (Long, Runnable) -> Unit
) : BaseViewModel(application) {

    val loading: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        val orderDetails = store.state.orderDetails
        val userCards = store.state.userCards

        val onChanged: (Boolean) -> Unit = {
            value = loadingValue
        }

        addSource(orderDetails.map { it.validating }, onChanged)
        addSource(userCards.map { it.loading }, onChanged)

        value = false
    }

    val loadingValue: Boolean
        get() = (store.state.orderDetails.value?.validating != false) ||
            (store.state.userCards.value?.loading != false)

    private val initialized: Boolean
        get() = store.state.navigation.value?.router != null

    private var orderDetails: OrderDetails? = null
    val requireOrderDetails: OrderDetails
        get() = requireNotNull(orderDetails)

    private val routePresenter: RoutePresenter =
        RoutePresenter(components.metrica, isDebug = false)

    fun bindRoutePresenter(
        fragmentManager: FragmentManager,
        closeWithResult: (RoutePresenter.Result) -> Unit
    ) {
        routePresenter.bind(fragmentManager, closeWithResult)
    }

    fun onBackPressed() {
        if (!routePresenter.onBackPressed()) {
            goBack()
        }
    }

    fun goBack() {
        store.dispatch(NavigationAction.Pull())
    }

    fun goToInitial() {
        store.dispatch(NavigationAction.Pull(Route.GetCards))
    }

    fun close() {
        store.dispatch(NavigationAction.Complete(null))
    }

    fun initialize() {
        if (loadingValue || initialized) {
            return
        }
        store.dispatch(NavigationAction.SetRouter(routePresenter))
        store.dispatch(GeneralActions.KickOff)
    }

    fun finishWithError(error: Error) {
        store.dispatch(NavigationAction.CompleteWithError(error))
    }

    fun save(bundle: Bundle) {
        StateRestoration.saveOrderDetails(bundle, requireOrderDetails)
    }

    fun restore(bundle: Bundle) {
        orderDetails = StateRestoration.loadOrderDetails(bundle)!!
    }

    fun restore(intent: Intent) {
        orderDetails = StateRestoration.loadOrderDetails(intent)!!
    }

    fun startNewCardBinding(fromCardsList: Boolean) {
        store.dispatch(UserCardsAction.StartNewCardBinding(fromCardsList))
    }

    fun initiateCompletion(checkoutResultValue: PaymentCheckoutResult?) {
        runPostponed(resources.getInteger(R.integer.yandexpay_main_window_close_delay).toLong()) {
            store.dispatch(NavigationAction.Complete(checkoutResultValue))
        }
    }

    fun runPostponed(delay: Long, block: Runnable) {
        postponeRunner(delay, block)
    }

    fun reauthorize() {
        invalidateAuthorization()
        requestAuthorization()
        store.dispatch(GeneralActions.ResetLoading) // Reset after we switch to authorizing.
    }

    private fun requestAuthorization() {
        store.dispatch(AuthorizationAction.Authorize)
    }

    private fun invalidateAuthorization() {
        store.dispatch(AuthorizationAction.InvalidateStoredToken)
    }

    class Factory(
        private val application: Application,
        private val store: Store,
        private val postponeRunner: (Long, Runnable) -> Unit
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            takeIf { modelClass == MainViewModel::class.java }?.let {
                MainViewModel(application, store, postponeRunner)
            } as T
    }
}
