package com.yandex.pay.core.storage

import androidx.lifecycle.MutableLiveData
import com.yandex.pay.core.data.LastUsedCard

internal class NotifyingCurrentCardStorage(
    private val signal: MutableLiveData<LastUsedCard>,
    private val storage: CurrentCardStorage,
) : CurrentCardStorage {
    override fun load(): LastUsedCard? = storage.load()

    override fun save(value: LastUsedCard): Unit =
        storage.save(value).also { signal.postValue(value) }

    override fun drop(): Unit = storage.drop().also { signal.postValue(null) }
}
