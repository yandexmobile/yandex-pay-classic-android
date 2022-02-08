package com.yandex.pay.core.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yandex.pay.core.actions.UserCardsAction
import com.yandex.pay.core.ui.views.interfaces.IHeaderView
import com.yandex.pay.core.ui.views.presenters.HeaderPresenter

internal class Confirm3DSViewModel(
    application: Application,
    private val parentViewModel: MainViewModel,
) : BaseViewModel(application) {
    private val headerPresenter: HeaderPresenter = HeaderPresenter()

    private val mutableLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = mutableLoading

    fun bind(headerView: IHeaderView) {
        val payload = HeaderPresenter.Payload.BackButton(::close)
        headerPresenter.present(payload, headerView)
    }

    private fun close() {
        parentViewModel.onBackPressed()
    }

    fun startLoading() {
        mutableLoading.value = true
    }

    fun doneLoading() {
        mutableLoading.value = false
    }

    fun onBackPressed() {
        parentViewModel.store.dispatch(UserCardsAction.CancelBinding.create(components.diehardApi))
    }

    internal class Factory(
        private val application: Application,
        private val parentViewModel: MainViewModel,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            takeIf { modelClass == Confirm3DSViewModel::class.java }?.let {
                Confirm3DSViewModel(application, parentViewModel)
            } as T
    }
}
