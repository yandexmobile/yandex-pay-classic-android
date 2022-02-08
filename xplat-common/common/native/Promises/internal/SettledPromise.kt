package com.yandex.xplat.common

class SettledPromise<V>(executorService: TaggedExecutorService, result: PromiseResult<V>) : AbstractPromise<V>(executorService) {

  internal constructor(executorService: TaggedExecutorService, value: V) : this(executorService, PromiseResult.Value(value))

  internal constructor(executorService: TaggedExecutorService, error: YSError) : this(executorService, PromiseResult.Error(error))

  init {
    settled.set(result)
  }
}
