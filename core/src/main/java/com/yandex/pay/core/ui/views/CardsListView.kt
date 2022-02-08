package com.yandex.pay.core.ui.views

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.pay.core.ui.fragments.CardsListAdapter
import com.yandex.pay.core.ui.views.interfaces.ICardItemView
import com.yandex.pay.core.ui.views.interfaces.ICardsListView

internal class CardsListView(private val view: RecyclerView) : ICardsListView {
    override var cards: List<ICardItemView> = emptyList()
        set(value) {
            field = value
            view.adapter?.notifyDataSetChanged()
        }

    override var selected: Int = 0
        set(value) {
            val oldValue = selected
            field = value
            if (oldValue != value) {
                view.adapter?.apply {
                    notifyItemChanged(oldValue)
                    notifyItemChanged(value)
                }
            }
        }

    override var onCardClick: (Int) -> Unit = { }
    override var onNewCardClick: () -> Unit = { }

    init {
        view.layoutManager = LinearLayoutManager(view.context)
        view.adapter = CardsListAdapter(this)
    }
}
