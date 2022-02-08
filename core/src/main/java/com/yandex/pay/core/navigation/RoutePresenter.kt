package com.yandex.pay.core.navigation

import android.content.Intent
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.yandex.pay.core.R
import com.yandex.pay.core.data.Error
import com.yandex.pay.core.data.PaymentCheckoutResult
import com.yandex.pay.core.events.Event
import com.yandex.pay.core.events.YPayMetrica
import com.yandex.pay.core.ui.fragments.*
import com.yandex.pay.core.utils.setUpFragmentAnimation

internal class RoutePresenter(private val logger: YPayMetrica, private val isDebug: Boolean) :
    Router {
    private lateinit var fragmentManager: FragmentManager
    private lateinit var closeWithResult: (Result) -> Unit

    fun bind(fragmentManager: FragmentManager, closeWithResult: (Result) -> Unit) {
        this.fragmentManager = fragmentManager
        this.closeWithResult = closeWithResult
    }

    sealed interface Result {
        object Cancelled : Result
        class Error(val intent: Intent) : Result
        class Ok(val intent: Intent) : Result
    }

    override fun push(route: Route) = when (route) {
        is Route.GetCards -> showFragment(PaymentFragment.newInstance(), route)
        is Route.Authorization -> showFragment(AuthorizationFragment.newInstance(), route)
        is Route.SelectCard -> showFragment(CardsListFragment.newInstance(), route)
        is Route.NewCardNumberBinding -> showFragment(BindCardFragment.newInstance(), route)
        is Route.Confirm3DS -> showFragment(
            Confirm3DSFragment.newInstance(route.uri, isDebug),
            route
        )
    }

    override fun pull(toRoute: Route?) {
        if (toRoute == null) {
            if (fragmentManager.backStackEntryCount > 1) {
                fragmentManager.popBackStack()
            } else {
                logger.log(Event.Cancelled(Event.Cancelled.ClosedEventMethod.BACK))
                closeWithResult(Result.Cancelled)
            }
        } else {
            if (fragmentManager.backStackEntryCount > 1) {
                fragmentManager.popBackStack(toRoute.name, 0)
            } else {
                logger.log(Event.Cancelled(Event.Cancelled.ClosedEventMethod.BACK))
                closeWithResult(Result.Cancelled)
            }
        }
    }

    override fun replace(route: Route) {
        if (fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        }
        push(route)
    }

    override fun finish(result: PaymentCheckoutResult?) {
        finishWithResult(result, null)
    }

    override fun finishWithError(error: Error) {
        finishWithResult(null, error)
    }

    fun onBackPressed(): Boolean =
        (fragmentManager.findFragmentById(R.id.yandexpay_container) as? BaseFragment<*>)?.onBackPressed() == true

    private fun showFragment(fragment: BaseFragment<*>, route: Route) {
        @IdRes val fragmentContainerId = R.id.yandexpay_container
        val currentFragmentRoot =
            fragmentManager.findFragmentById(fragmentContainerId)?.view
        fragmentManager.commit(true) {
            setUpFragmentAnimation(fragment, currentFragmentRoot, route.animated)
            setReorderingAllowed(true)
            addToBackStack(route.name)
            replace(fragmentContainerId, fragment)
        }
    }

    private fun finishWithResult(result: PaymentCheckoutResult?, error: Error?) {
        when {
            result == null && error == null -> closeWithResult(Result.Cancelled)
            result != null && error == null -> {
                logger.log(Event.Success)
                closeWithResult(
                    Result.Ok(
                        Intent().also(result::addToIntent),
                    )
                )
            }
            result == null && error != null -> {
                logger.log(Event.Failure(error.code.name))
                closeWithResult(Result.Error(Intent().also {
                    PaymentCheckoutResult.addErrorToIntent(
                        it,
                        error
                    )
                }))
            }
        }
    }
}
