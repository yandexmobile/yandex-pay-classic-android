package com.yandex.pay.core.storage

import com.yandex.pay.core.data.CardID
import com.yandex.pay.core.data.LastUsedCard
import com.yandex.pay.core.data.UserCard

internal class CurrentCardChanger(private val storage: CurrentCardStorage) {
    val value: LastUsedCard?
        get() = storage.load()

    fun change(items: List<UserCard>): CardID? {
        if (items.isEmpty()) {
            dropUserCard()
            return null
        }

        val firstItem = items.first()

        val currentCardID = storage.load()?.cardID
        if (currentCardID == null) {
            // There's no current card saved, so we assume
            // that the first one incoming is the selected one.
            changeTo(firstItem)
            return firstItem.id
        }

        val currentAmongIncoming = items.find { it.id == currentCardID }
        if (currentAmongIncoming == null) {
            // Current card not found among incoming
            // Reset to the first one.
            changeTo(firstItem)
            return firstItem.id
        }
        return currentAmongIncoming.id
    }

    fun changeTo(userCard: UserCard?) {
        userCard ?: return
        storage.save(LastUsedCard.from(userCard))
    }

    private fun dropUserCard() {
        storage.drop()
    }
}
