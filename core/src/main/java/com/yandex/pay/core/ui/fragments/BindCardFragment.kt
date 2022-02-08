package com.yandex.pay.core.ui.fragments

import android.animation.ValueAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import com.yandex.pay.core.R
import com.yandex.pay.core.databinding.YandexpayBindCardFragmentBinding
import com.yandex.pay.core.ui.views.ErrorTextView
import com.yandex.pay.core.utils.Keyboard
import com.yandex.pay.core.utils.visible
import com.yandex.pay.core.viewmodels.BindCardViewModel

internal class BindCardFragment :
    BaseFragment<BindCardViewModel>(R.layout.yandexpay_bind_card_fragment), OnAppearance {
    private var binding: YandexpayBindCardFragmentBinding? = null
    private val requireBinding: YandexpayBindCardFragmentBinding
        get() = requireNotNull(binding)

    override val viewModel: BindCardViewModel by viewModels {
        BindCardViewModel.Factory(requireActivity().application, parentViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = YandexpayBindCardFragmentBinding.bind(view)

        setupViewModel(savedInstanceState)

        setupHeader()
        setupCardNumberInput()
        setupExpirationDateInput()
        setupCvnInput()
        setupBindCardButton()
    }

    override fun appearanceAnimationCompleted() {
        Handler(Looper.getMainLooper()).postDelayed({
            requireBinding.yandexpayCardNumberInput.gainFocus()
        }, 250)
    }

    override fun onDestroyView() {
        viewModel.unbind(requireBinding.yandexpayCardNumberInput)
        viewModel.unbind(requireBinding.yandexpayCvnInput)
        viewModel.unbind(requireBinding.yandexpayExpirationDateInput)
        binding = null
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.save(outState)
    }

    override fun onBackPressed(): Boolean {
        looseFocus()
        return viewModel.onBackPressed()
    }

    private fun bindCardButtonTapped() {
        when (viewModel.currentState.value!!) {
            BindCardViewModel.State.CARD_NUMBER -> {
                val error = viewModel.validateCardNumberInput()
                if (error == null) {
                    viewModel.moveToOtherCardDetailsInput()
                } else {
                    requireBinding.yandexpayCardNumberInput.showError(error)
                }
            }
            BindCardViewModel.State.CARD_DETAILS -> {
                if (viewModel.isExpirationDateValid && viewModel.isCvnValid) {
                    looseFocus()
                    viewModel.completeCardDataEntry()
                } else if (!showAnyError()) {
                    moveFocus()
                } else Unit
            }
            BindCardViewModel.State.BINDING -> Unit
            BindCardViewModel.State.DONE -> Unit
        }
    }

    private fun expandCardNumber(completion: (() -> Unit)? = null) {
        val cardNumberInput = requireBinding.yandexpayCardNumberInput
        viewModel.expandCardNumberInput()
        val setWidth = { w: Int ->
            cardNumberInput.updateLayoutParams {
                width = w
            }
        }

        val from = cardNumberInput.measuredWidth
        val to = (cardNumberInput.parent as View).measuredWidth

        // Animate
        ValueAnimator.ofInt(from, to).apply {
            duration =
                resources.getInteger(android.R.integer.config_longAnimTime).toLong()
            interpolator = DecelerateInterpolator()
            addUpdateListener { setWidth(it.animatedValue as Int) }
            start()
        }.doOnEnd {
            // Set actual layout params after animation
            setWidth(LinearLayout.LayoutParams.MATCH_PARENT)
            completion?.invoke()
        }
    }

    private fun collapseCardNumber(completion: (() -> Unit)? = null) {
        if (!viewModel.cardNumberExpanded) {
            return
        }
        val cardNumberInput = requireBinding.yandexpayCardNumberInput
        val from = cardNumberInput.measuredWidth
        val to = resources.getDimension(R.dimen.yandexpay_card_number_input_collapsed_width)
            .toInt()

        ValueAnimator.ofInt(from, to).apply {
            duration =
                resources.getInteger(android.R.integer.config_longAnimTime).toLong()
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                cardNumberInput.updateLayoutParams {
                    width = animation.animatedValue as Int
                }
            }
            doOnEnd {
                completion?.invoke()
                viewModel.collapseCardNumberInput()
            }
            start()
        }
    }

    private fun setupHeader() {
        viewModel.bind(requireBinding.yandexpayHeaderView)
    }

    private fun setupBindCardButton() {
        viewModel.updateBindCardButtonState()
        viewModel.bind(requireBinding.yandexpayBindCardButton, ::bindCardButtonTapped)
    }

    private fun setupViewModel(savedInstanceState: Bundle?) {
        viewModel.currentState.distinctUntilChanged()
            .observe(viewLifecycleOwner, ::applyStateChange)

        viewModel.bindCardButtonEnabled.distinctUntilChanged().observe(viewLifecycleOwner) {
            applyBindCardButtonStateChange()
        }

        viewModel.error.observe(viewLifecycleOwner, ::presentError)

        viewModel.recoverableError.observe(viewLifecycleOwner) {
            if (it != null) {
                viewModel.setupGlobalRecoverableErrorCleanup()
            }
        }

        viewModel.setupLocalServiceErrorCleanup()

        viewModel.restore(savedInstanceState)
    }

    private fun setupCardNumberInput() {
        with(requireBinding.yandexpayCardNumberInput) {
            viewModel.bind(this)
            onFocusGained = viewModel::moveToCardNumberInput
            onKeyboardAction = ::bindCardButtonTapped
            onValueChanged = ::onCardNumberChanged
        }
    }

    private fun setupExpirationDateInput() {
        with(requireBinding.yandexpayExpirationDateInput) {
            viewModel.bind(this)
            onKeyboardAction = ::bindCardButtonTapped
            onValueChanged = ::onExpirationDateChanged
        }
    }

    private fun setupCvnInput() {
        with(requireBinding.yandexpayCvnInput) {
            viewModel.bind(this)
            onKeyboardAction = ::bindCardButtonTapped
            onValueChanged = ::onCvnChanged
        }
    }

    private fun looseFocus() {
        requireBinding.yandexpayCardNumberInput.looseFocus()
        requireBinding.yandexpayExpirationDateInput.looseFocus()
        requireBinding.yandexpayCvnInput.looseFocus()
        Keyboard.hide(requireBinding.root)
    }

    private fun moveFocus(): Boolean {
        val dateInput = requireBinding.yandexpayExpirationDateInput
        val hasDate = viewModel.hasExpirationDate
        val cvnInput = requireBinding.yandexpayCvnInput
        val hasCvn = viewModel.hasCvn

        return when {
            hasDate && hasCvn -> false
            hasDate && dateInput.hasFocus -> {
                cvnInput.gainFocus(); true
            }
            hasCvn && cvnInput.hasFocus -> {
                dateInput.gainFocus(); true
            }
            else -> true // Do not move it anywhere, keep it where it is
        }
    }

    private fun onCardNumberChanged() {
        viewModel.onCardNumberValueChanged()
    }

    private fun onExpirationDateChanged() {
        moveFocus()
        viewModel.onExpirationDateValueChanged()
    }

    private fun onCvnChanged() {
        moveFocus()
        viewModel.onCvnValueChanged()
    }

    private fun showAnyError(): Boolean = when {
        requireBinding.yandexpayExpirationDateInput.hasFocus -> viewModel.validateExpirationDateInput()
            ?.also(requireBinding.yandexpayExpirationDateInput::showError) != null

        requireBinding.yandexpayCvnInput.hasFocus -> viewModel.validateCvnInput()
            ?.also(requireBinding.yandexpayCvnInput::showError) != null

        requireBinding.yandexpayCardNumberInput.hasFocus -> viewModel.validateCardNumberInput()
            ?.also(requireBinding.yandexpayCardNumberInput::showError) != null

        else -> false
    }

    private fun applyStateChange(state: BindCardViewModel.State) {
        when (state) {
            BindCardViewModel.State.CARD_NUMBER -> switchToCardNumber()
            BindCardViewModel.State.CARD_DETAILS -> switchToCardDetails()
            BindCardViewModel.State.BINDING, BindCardViewModel.State.DONE -> updateControlsState()
        }
    }

    private fun applyBindCardButtonStateChange() {
        setControlsAvailability(true)
        setupBindCardButton()
    }

    private fun updateControlsState() {
        setControlsAvailability(false)
        setDetailsVisibility(true)
        setupBindCardButton()
    }

    private fun switchToCardNumber() {
        switchToDetailEntry(false, ::expandCardNumber)
    }

    private fun switchToCardDetails() {
        switchToDetailEntry(true) {
            collapseCardNumber { requireBinding.yandexpayExpirationDateInput.gainFocus() }
        }
    }

    private fun switchToDetailEntry(detailsVisible: Boolean, cardNumberSetup: () -> Unit) {
        setDetailsVisibility(detailsVisible)
        setControlsAvailability(true)
        cardNumberSetup()
        setupBindCardButton()
    }

    private fun setDetailsVisibility(visible: Boolean) {
        requireBinding.yandexpayExpirationDateInput.visible = visible
        requireBinding.yandexpayCardNumberToExpirationDateSpace.visible = visible
        requireBinding.yandexpayCvnInput.visible = visible
        requireBinding.yandexpayExpirationDateToCvnSpace.visible = visible
    }

    private fun setControlsAvailability(enabled: Boolean) {
        with(requireBinding) {
            yandexpayBindCardButton.disabled = !enabled
            yandexpayCardNumberInput.disabled = !enabled
            yandexpayExpirationDateInput.disabled = !enabled
            yandexpayCvnInput.disabled = !enabled
        }
    }

    private fun presentError(error: String?) {
        viewModel.bind(ErrorTextView(requireBinding.yandexpayErrorText))
        viewModel.bind(requireBinding.yandexpayBindCardButton, ::bindCardButtonTapped)
    }

    internal companion object {
        fun newInstance(): BindCardFragment = BindCardFragment().apply { arguments = Bundle() }
    }
}
