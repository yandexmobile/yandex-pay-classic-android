package com.yandex.pay.core.viewmodels

import android.app.Application
import android.content.Context
import android.content.res.Resources
import androidx.lifecycle.AndroidViewModel
import com.yandex.pay.core.YandexPayLib
import com.yandex.pay.core.actions.GeneralActions
import com.yandex.pay.core.data.Error
import com.yandex.pay.core.data.ErrorType
import com.yandex.pay.core.di.ComponentsHolder
import com.yandex.pay.core.events.Event
import com.yandex.pay.core.utils.AuthorizationException
import com.yandex.pay.core.utils.ErrorDescriptor
import com.yandex.pay.core.utils.ValidationException

internal open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    protected val components: ComponentsHolder
        get() = YandexPayLib.instance.componentsHolder

    protected val resources: Resources
        get() = getApplication<Application>().resources

    protected val applicationContext: Context
        get() = getApplication<Application>().applicationContext

    protected fun processError(
        error: Throwable,
        parentViewModel: MainViewModel,
    ) = with(parentViewModel) {
        when (error) {
            is AuthorizationException -> parentViewModel.reauthorize()
            is ValidationException -> store.dispatch(
                GeneralActions.SetError(
                    ErrorType.Fatal(
                        Error(
                            error.validationResult
                        )
                    )
                )
            )
            else -> store.dispatch(
                GeneralActions.SetError(
                    ErrorType.Recoverable(
                        ErrorDescriptor(
                            error.localizedMessage
                        )
                    )
                )
            )
        }
    }

    fun logEvent(event: Event) {
        components.metrica.log(event)
    }
}
