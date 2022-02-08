package com.yandex.pay.core.state

import com.yandex.pay.core.data.UserCard
import com.yandex.pay.core.infra.State

internal data class UserCardsState(
    val cards: List<UserCard>?,
    val selected: Int,
    val loading: Boolean,
) : State {
    val hasSelected: Boolean
        get() = selected == NO_SELECTED

    internal companion object {
        const val NO_SELECTED: Int = -1

        fun create(): UserCardsState = UserCardsState(null, NO_SELECTED, false)
    }
}
