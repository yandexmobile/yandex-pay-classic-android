package com.yandex.pay.core.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.yandex.pay.core.R
import com.yandex.pay.core.databinding.YandexpayAuthorizationFragmentBinding
import com.yandex.pay.core.utils.visible
import com.yandex.pay.core.viewmodels.AuthorizationViewModel

internal class AuthorizationFragment :
    BaseFragment<AuthorizationViewModel>(R.layout.yandexpay_authorization_fragment) {
    override val viewModel: AuthorizationViewModel by viewModels(factoryProducer = {
        AuthorizationViewModel.Factory(requireActivity().application, this, parentViewModel)
    })

    private var binding: YandexpayAuthorizationFragmentBinding? = null
    private val requireBinding: YandexpayAuthorizationFragmentBinding
        get() = requireNotNull(binding)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let(viewModel::restore)

        binding = YandexpayAuthorizationFragmentBinding.bind(view)

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            requireBinding.yandexpayProgressBar.visible = loading
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadToken()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.save(outState)
    }

    internal companion object {
        fun newInstance(): AuthorizationFragment = AuthorizationFragment()
    }
}
