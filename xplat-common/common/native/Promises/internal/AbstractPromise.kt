package com.yandex.xplat.common

import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicInteger

abstract class AbstractPromise<V> protected constructor(executorService: TaggedExecutorService) : Kromise<V>(executorService) {
  private val children = arrayListOf<AbstractPromise<*>>()
  private val id = nextId.getAndIncrement()

  protected val settled = ResolvableFuture.create<PromiseResult<V>>()!!

  override val isDone: Boolean
    get() = settled.isDone

  override fun cancel() {
    settled.cancel(true)
  }

  protected var hasHandlers: Boolean = false

  private fun walk(visitor: AbstractPromise<*>.() -> Unit) {
    children.forEach {
      it.visitor()
      it.walk(visitor)
    }
  }

  override fun <X> addHandler(
      executorService: TaggedExecutorService,
      onResolved: (V) -> X,
      onRejected: ((YSError) -> X)?
  ): XPromise<X> {
    hasHandlers = true
    val p = deferred<X>(executorService)

    settled.addListener(Runnable {
      if (isDone) {
        try {
          when (val result = settled.get()) {
            is PromiseResult.Value -> p.resolve(onResolved(result.value))
            is PromiseResult.Error -> {
              if (onRejected != null) {
                p.resolve(onRejected(result.error))
              } else {
                p.reject(result.error)
              }
            }
          }
        } catch (e: Throwable) {
          p.reject(buildFailure(e))
        }
      }
    }, executorService)

    children += p.promise as AbstractPromise<*>
    return p.promise
  }

  override fun <X> addFlatteningHandler(
      executorService: TaggedExecutorService,
      onResolved: (V) -> XPromise<X>,
      onRejected: ((YSError) -> XPromise<X>)?
  ): XPromise<X> {
    hasHandlers = true
    val p = deferred<X>(executorService)

    settled.addListener(Runnable {
      if (isDone) {
        try {
          when (val result = settled.get()) {
            is PromiseResult.Value -> onResolved(result.value).both(p::resolve, p::reject)
            is PromiseResult.Error -> {
              if (onRejected != null) {
                onRejected(result.error).both(p::resolve, p::reject)
              } else {
                p.reject(result.error)
              }
            }
          }
        } catch (e: Throwable) {
          p.reject(buildFailure(e))
        }
      }
    }, executorService)

    children += p.promise as AbstractPromise<*>
    return p.promise
  }

  protected val execService: ExecutorService
    get() = executorService

  companion object {
    private var nextId = AtomicInteger(0)
  }
}
