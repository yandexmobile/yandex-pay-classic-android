package com.yandex.pay.core.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import com.yandex.pay.core.R
import com.yandex.pay.core.data.ErrorType
import com.yandex.pay.core.databinding.YandexpayPaymentFragmentBinding
import com.yandex.pay.core.ui.views.LicenseAgreementView
import com.yandex.pay.core.viewmodels.PaymentViewModel

internal class PaymentFragment :
    BaseFragment<PaymentViewModel>(R.layout.yandexpay_payment_fragment) {
    private var binding: YandexpayPaymentFragmentBinding? = null
    private val requireBinding: YandexpayPaymentFragmentBinding
        get() = requireNotNull(binding)

    override val viewModel: PaymentViewModel by viewModels(factoryProducer = {
        PaymentViewModel.Factory(requireActivity().application, parentViewModel)
    })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = YandexpayPaymentFragmentBinding.bind(view)
        viewModel.userProfile.distinctUntilChanged().observe(viewLifecycleOwner) {
            updateAvatar()
        }
        viewModel.uiState.distinctUntilChanged().observe(viewLifecycleOwner, ::updateUIState)
        updateHeader()
        updateLicenseAgreement()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadUserCards()
        viewModel.loadUserProfile()
    }

    private fun updateContent() {
        updateUserCardsDisplay()
        updateCheckoutButton()
        setControlsAvailability(viewModel.operationsAvailability)
    }

    private fun setControlsAvailability(enabled: Boolean) {
        with(requireBinding) {
            yandexpayLicenseAgreementText.isEnabled = enabled
            yandexpayHeaderView.isEnabled = enabled
            yandexpayCardItem.isEnabled = enabled
            updateAvatar()
        }
    }

    private fun updateHeader() {
        viewModel.bindView(requireBinding.yandexpayHeaderView)
    }

    private fun updateAvatar() {
        viewModel.bindView(requireBinding.yandexpayHeaderView.avatar, ::onAvatarTapped)
    }

    private fun onAvatarTapped() {
        viewModel.logout()
    }

    private fun updateUserCardsDisplay() {
        viewModel.bindView(requireBinding.yandexpayCardItem)
    }

    private fun updateCheckoutButton() {
        viewModel.bindView(requireBinding.yandexpayCheckoutButton)
    }

    private fun updateLicenseAgreement() {
        viewModel.bindView(
            LicenseAgreementView(requireBinding.yandexpayLicenseAgreementText),
            requireActivity(),
        )
    }

    private fun hideError() {
        updateCheckoutButton()
        setControlsAvailability(viewModel.operationsAvailability)
    }

    private fun updateUIState(state: PaymentViewModel.UIState) {
        hideError()
        when (state) {
            PaymentViewModel.UIState.CheckingOut,
            PaymentViewModel.UIState.Loading,
            PaymentViewModel.UIState.NoCards,
            is PaymentViewModel.UIState.Normal -> updateContent()

            PaymentViewModel.UIState.Done -> updateContent().also { viewModel.initiateCompletion() }
            is PaymentViewModel.UIState.Error -> switchToErrorState(state.error)
        }
    }

    private fun switchToErrorState(error: ErrorType?) {
        when (error) {
            null -> {
                setControlsAvailability(true)
                viewModel.runAfterError()
            }
            is ErrorType.Recoverable, is ErrorType.Fatal -> {
                setControlsAvailability(false)
                updateCheckoutButton()
            }
        }
    }

    internal companion object {
        fun newInstance() = PaymentFragment()
    }
}
