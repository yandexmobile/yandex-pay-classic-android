package com.yandex.pay.core.storage

import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.core.content.edit
import com.yandex.pay.core.data.CardID
import com.yandex.pay.core.data.CardNetwork
import com.yandex.pay.core.data.LastUsedCard

internal class SharedPreferencesCurrentCardStorage(
    private val sharedPreferences: SharedPreferences,
) : CurrentCardStorage {
    override fun load(): LastUsedCard? =
        sharedPreferences.getString(DATA_KEY, null)?.let(::parse)

    @SuppressLint("ApplySharedPref")
    override fun save(value: LastUsedCard) {
        sharedPreferences.edit { putString(DATA_KEY, stringify(value)) }
    }

    @SuppressLint("ApplySharedPref")
    override fun drop() {
        sharedPreferences.edit { remove(DATA_KEY) }
    }

    private fun parse(value: String): LastUsedCard? {
        val parts = value.split(DELIMITER)
        return takeIf { parts.size == 3 }?.let {
            LastUsedCard(CardID.from(parts[0]), CardNetwork.valueOf(parts[1]), parts[2])
        }
    }

    private fun stringify(value: LastUsedCard): String =
        listOf(value.cardID.value, value.network.name, value.last4Digits).joinToString(DELIMITER)

    private companion object {
        const val DATA_KEY: String = "YANDEXPAY_CURRENT_CARD_DATA"
        const val DELIMITER: String = " "
    }
}
