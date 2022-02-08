package com.yandex.xplat.common

import java.util.concurrent.Callable
import java.util.concurrent.FutureTask

class SettablePromise<V>(executorService: TaggedExecutorService, executor: Executor<V>) : AbstractPromise<V>(executorService) {
  private val execFuture = FutureTask(Callable {
    try {
      executor(this::resolve, this::reject)
    } catch (e: Throwable) {
      reject(buildFailure(e.cause ?: e))
    }
  })

  internal fun resolve(value: V) {
    settled.set(PromiseResult.Value(value))
    execFuture.cancel(true)
  }

  internal fun reject(error: YSError) {
    settled.set(PromiseResult.Error(error))
    execFuture.cancel(true)
    if (!hasHandlers) {
      onUnhandledException(error)
    }
  }

  override fun cancel() {
    execFuture.cancel(true)
    super.cancel()
  }

  init {
    execService.execute(execFuture)
  }
}
