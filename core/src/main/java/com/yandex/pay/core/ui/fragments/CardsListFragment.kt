package com.yandex.pay.core.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.yandex.pay.core.R
import com.yandex.pay.core.databinding.YandexpayCardsListFragmentBinding
import com.yandex.pay.core.ui.views.CardsListView
import com.yandex.pay.core.viewmodels.CardsListViewModel

internal class CardsListFragment :
    BaseFragment<CardsListViewModel>(R.layout.yandexpay_cards_list_fragment) {
    override val viewModel: CardsListViewModel by viewModels(factoryProducer = {
        CardsListViewModel.Factory(requireActivity().application, parentViewModel)
    })

    private var binding: YandexpayCardsListFragmentBinding? = null
    private val requireBinding: YandexpayCardsListFragmentBinding
        get() = requireNotNull(binding)

    private var cardsListView: CardsListView? = null
    private val requireCardsListView: CardsListView
        get() = requireNotNull(cardsListView)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = YandexpayCardsListFragmentBinding.bind(view)
        cardsListView = CardsListView(requireBinding.yandexpayCardsList)
        viewModel.selectedCardChanged.observe(viewLifecycleOwner) {
            updateCards()
        }
        updateHeader()
        updateCards()
    }

    override fun onDestroyView() {
        binding = null
        cardsListView = null
        super.onDestroyView()
    }

    private fun updateCards() {
        viewModel.updateCardsList(requireCardsListView)
    }

    private fun updateHeader() {
        viewModel.updateHeader(requireBinding.yandexpayHeaderView)
    }

    internal companion object {
        fun newInstance() = CardsListFragment()
    }
}
