package com.yandex.xplat.common

class DeferImpl<V>(executorService: TaggedExecutorService) : Defer<V> {

  override val promise: XPromise<V> = SettablePromise(executorService) { _, _ -> }

  override fun resolve(result: V) {
    (promise as SettablePromise<V>).resolve(result)
  }

  override fun reject(error: YSError) {
    (promise as SettablePromise<V>).reject(error)
  }
}
