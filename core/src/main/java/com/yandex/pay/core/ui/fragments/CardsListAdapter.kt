package com.yandex.pay.core.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yandex.pay.core.databinding.YandexpayCardListItemBinding
import com.yandex.pay.core.ui.views.interfaces.ICardItemView
import com.yandex.pay.core.ui.views.interfaces.ICardsListView
import com.yandex.pay.core.ui.views.interfaces.from
import com.yandex.pay.core.ui.views.presenters.CardItemPresenter

internal class CardsListAdapter(
    private val view: ICardsListView,
) : RecyclerView.Adapter<CardsListAdapter.CardItemViewHolder>() {
    private lateinit var presenter: CardItemPresenter

    private fun numberOfCards(cards: List<*>?): Int = cards?.size ?: 0

    class CardItemViewHolder(private val binding: YandexpayCardListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val cardItemView: ICardItemView
            get() = binding.yandexpayCardItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardItemViewHolder {
        if (!::presenter.isInitialized) {
            presenter = CardItemPresenter(parent.resources, ICardItemView.AccessoryType.Checkmark)
        }
        return CardItemViewHolder(
            YandexpayCardListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CardItemViewHolder, position: Int) {
        val cards = view.cards
        val numberOfCards = numberOfCards(cards)

        when {
            position < numberOfCards -> bindCardItem(cards[position], holder)
            position < numberOfCards + EXTRA_ROW_ITEMS -> bindAddNewCard(
                view.onNewCardClick,
                holder
            )
            else -> throw IndexOutOfBoundsException("position is greater than cards amount and extra items")
        }
    }

    override fun getItemCount(): Int = numberOfCards(view.cards).let { numOfCards ->
        if (numOfCards == MAX_ALLOWED_CARD) numOfCards else (numOfCards + EXTRA_ROW_ITEMS)
    }

    private fun bindAddNewCard(onNewCardClick: () -> Unit, holder: CardItemViewHolder) {
        presenter.present(CardItemPresenter.Payload.AddItem(onNewCardClick), holder.cardItemView)
    }

    private fun bindCardItem(card: ICardItemView, holder: CardItemViewHolder) {
        holder.cardItemView.from(card, true)
    }

    companion object {
        // This is 'Add new card' item
        const val EXTRA_ROW_ITEMS: Int = 1
        const val MAX_ALLOWED_CARD: Int = 5
    }
}
