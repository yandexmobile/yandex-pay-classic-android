package com.yandex.pay.core.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yandex.pay.core.YandexPayLib
import com.yandex.pay.core.data.PaymentCheckoutResult
import com.yandex.pay.core.databinding.YandexpayMainActivityBinding
import com.yandex.pay.core.events.Event
import com.yandex.pay.core.navigation.RoutePresenter
import com.yandex.pay.core.viewmodels.MainViewModel

internal class MainActivity : AppCompatActivity() {
    private lateinit var binding: YandexpayMainActivityBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val viewModel: MainViewModel by viewModels(factoryProducer = {
        MainViewModel.Factory(
            application,
            YandexPayLib.instance.componentsHolder.buildStore()
        ) { delay, block ->
            Handler(mainLooper).postDelayed(block, delay)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = YandexpayMainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomSheetBehavior = attachBottomSheetBehaviour(binding.yandexpayWindowWithHandle)
        binding.yandexpayCoordinator.setOnClickListener {
            viewModel.logEvent(Event.Cancelled(Event.Cancelled.ClosedEventMethod.TAP))
            viewModel.close()
        }
        setupViewModel(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        viewModel.initialize()
    }

    override fun onBackPressed() {
        viewModel.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.save(outState)
    }

    private fun setupViewModel(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            viewModel.restore(savedInstanceState)
        } else {
            viewModel.restore(intent)
        }
        viewModel.bindRoutePresenter(supportFragmentManager, ::finishWithResult)
    }

    private fun <T : ViewGroup> attachBottomSheetBehaviour(view: T): BottomSheetBehavior<T> =
        BottomSheetBehavior.from(view).apply {
            state = BottomSheetBehavior.STATE_EXPANDED
            addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        viewModel.logEvent(Event.Cancelled(Event.Cancelled.ClosedEventMethod.SWIPE))
                        viewModel.close()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
            })
        }

    private fun finishWithResult(result: RoutePresenter.Result) {
        when (result) {
            is RoutePresenter.Result.Cancelled -> setResult(RESULT_CANCELED)
            is RoutePresenter.Result.Ok -> setResult(RESULT_OK, result.intent)
            is RoutePresenter.Result.Error -> setResult(
                PaymentCheckoutResult.RESULT_ERROR_CODE,
                result.intent,
            )
        }
        finish()
    }

    internal companion object {
        @JvmStatic
        fun createIntent(context: Context): Intent = Intent(
            context,
            MainActivity::class.java
        )
    }
}
