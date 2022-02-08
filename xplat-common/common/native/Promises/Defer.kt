package com.yandex.xplat.common

/**
 *
 * @param V Type of the promise's value.
 */
interface Defer<V> {
  /**
   * The underlying promise
   */
  val promise: XPromise<V>

  /**
   * Resolve's the promise with the given [result], if still pending.
   *
   * @param result the result of the promise
   */
  fun resolve(result: V)

  /**
   * Rejects the underlying promise with the given [error]
   *
   * @param error the reason of rejection
   */
  fun reject(error: YSError)
}

fun <V> deferred(on: TaggedExecutorService): Defer<V> = DeferImpl(on)
