@file:Suppress("unused")

package com.yandex.xplat.common

import java.util.concurrent.*
import okhttp3.internal.Util

sealed class TaggedExecutorService(executorService: ExecutorService) : ExecutorService by executorService {
    class Default(executorService: ExecutorService) : TaggedExecutorService(executorService)
    class Awaiting(executorService: ExecutorService) : TaggedExecutorService(executorService)
    class Specialized(val specialty: String, executorService: ExecutorService) : TaggedExecutorService(executorService)

    fun moveFromAwaiting(): TaggedExecutorService = if (this is Awaiting) DefaultExecutorService else this
}

/**
 * Represents the eventual completion (or failure) of an asynchronous
 * operation, and its resulting value (or error).
 *
 * Creating Promises:
 * @see promise
 */
abstract class Kromise<V>(
    protected val executorService: TaggedExecutorService = DefaultExecutorService
) : XPromise<V>() {
    /**
     * Returns a pending promise. When the current promise settles,
     * the handler function, either [onResolved] or [onRejected], gets
     * called asynchronously with the settlement value if the current
     * promise resolves, or the reason if the current promise rejects.
     *
     * If the handler function:
     *
     * - returns a value, the returned promise gets resolved with the
     * returned value as its value;
     * - throws an error, the promise returned by then() gets rejected
     * with the exception as the reason;
     *
     * @param onResolved handler called if the current promise is
     * resolved. This function has a single argument, the fulfillment value
     *
     * @param onRejected handler called if the current promise is
     * rejected. This function has a single argument, the [Error]
     * reason of rejection.
     *
     * @return XPromise
     */
    override fun <X> both(onResolved: (V) -> X, onRejected: (YSError) -> X): XPromise<X> = addHandler(
        onResolved = onResolved,
        onRejected = onRejected
    )

    override fun <X> flatBoth(
        onResolved: (V) -> XPromise<X>,
        onRejected: (YSError) -> XPromise<X>
    ): XPromise<X> = addFlatteningHandler(
        onResolved = onResolved,
        onRejected = onRejected
    )

    /**
     * Returns a pending promise. Accepts a single handler for fulfillment.
     * If the original promise rejects, the returned promise rejects
     * with the original's reason of rejection.
     *
     * This overload is best suited for chaining promises.
     *
     * @param onResolved handler called if the current promise is
     * resolved. This function has a single argument, the fulfillment value
     *
     * @return a pending promise.
     */
    override fun <X> then(onResolved: (V) -> X): XPromise<X> = addHandler(onResolved = onResolved)

    override fun <X> flatThen(onResolved: (V) -> XPromise<X>): XPromise<X> = addFlatteningHandler(onResolved = onResolved)

    /**
     * Returns a pending promise which handles rejection cases only.
     * If the original promise rejects, [onRejected] is called with the
     * reason of rejection.
     *
     * @param onRejected handler called if the current promise is
     * rejected. This function has a single argument, the [Failure]
     * reason of rejection.
     *
     * @return a pending promise.
     */
    override fun catch(onRejected: (YSError) -> V): XPromise<V> = addHandler(onResolved = { it }, onRejected = onRejected)

    override fun flatCatch(onRejected: (YSError) -> XPromise<V>): XPromise<V> = addFlatteningHandler(onResolved = { resolve(it) }, onRejected = onRejected)
    override fun failed(onRejected: (YSError) -> Unit) {
        addHandler<Unit>(onResolved = { }, onRejected = onRejected)
    }

    /**
     * Returns a pending promise. The single argument [onFinally] is called
     * when the promise settles with either the fulfillment value or
     * rejection reason.
     *
     */
    override fun finally(onFinally: () -> Unit): XPromise<V> = addFlatteningHandler(
        onResolved = {
            onFinally()
            resolve(it)
        },
        onRejected = {
            onFinally()
            reject(it)
        }
    )
    private fun <X> finallyWithResult(handler: (PromiseResult<V>) -> X): XPromise<X> = addHandler(
        onResolved = { handler(PromiseResult.Value(it)) },
        onRejected = { handler(PromiseResult.Error(it)) }
    )

    /**
     * True if the promise has settled.
     */
    protected abstract val isDone: Boolean

    /**
     * Cancels a pending. cancel() has no effect if the promise
     * has already settled. Otherwise the promise will be rejected
     * with a [InterruptedException].
     */
    abstract fun cancel()

    internal abstract fun <X> addHandler(
        executorService: TaggedExecutorService = this.executorService.moveFromAwaiting(),
        onResolved: (V) -> X,
        onRejected: ((YSError) -> X)? = null
    ): XPromise<X>

    internal abstract fun <X> addFlatteningHandler(
        executorService: TaggedExecutorService = this.executorService.moveFromAwaiting(),
        onResolved: (V) -> XPromise<X>,
        onRejected: ((YSError) -> XPromise<X>)? = null
    ): XPromise<X>

    companion object {
        /**
         * Returns a promise that resolves or rejects as soon as
         * one of the promises in the iterable resolves or rejects,
         * with the value or reason from that promise.
         *
         * @param promises [Iterable] list of promises.
         *
         * @return A pending XPromise that resolves or rejects
         * asynchronically as soon as one of the promises in the given
         * iterable resolves or rejects, adopting that first promise's
         * value as its value.
         *
         * If the iterable passed is empty, the promise returned will be forever
         * pending.
         */
        @JvmStatic
        internal fun <V> race(on: TaggedExecutorService, promises: YSArray<XPromise<V>>): XPromise<V> =
            promise(on) { resolve, reject ->
                promises.forEach { item: XPromise<V> ->
                    item.both(resolve, reject)
                }
            }

        /**
         *  Returns a single XPromise that resolves when all of the promises
         *  in the iterable argument have resolved or when the iterable argument
         *  contains no promises. It rejects immediately with the reason of the
         *  first promise that rejects without waiting for the other promises
         *  to settle.
         *
         * @param promises [Iterable] list of promises.
         *
         * @return XPromise which resolves to a [List] of all resolved values
         *   from the supplied list of [promises], if all promises resolve,
         *   or assumes the reason of rejection of the first promise that rejects.
         */
        @JvmStatic
        internal fun <V> all(on: TaggedExecutorService, promises: YSArray<XPromise<V>>): XPromise<YSArray<V>> {
            // ConcurrentHashMap does not allow "null" usage for keys and values.
            // However, "null" is a perfectly reasonable value for Promises.
            // Let's just wrap whatever value we are given in order to support this "null" use-case.
            class Wrapped<T>(val item: T)

            val items = promises.toList()
            val results = ConcurrentHashMap<Int, Wrapped<V>>()
            val latch = CountDownLatch(items.size)

            return promise(on) { resolve, reject ->
                items.forEachIndexed { index, item ->
                    (item as Kromise<V>).finallyWithResult {
                        when (it) {
                            is PromiseResult.Value -> results[index] = Wrapped(it.value)
                            is PromiseResult.Error -> reject(it.error)
                        }
                        latch.countDown()
                    }
                }

                latch.await()
                if (!(this as Kromise<*>).isDone) {
                    val result = items.indices.map { results[it]!!.item }.toMutableList()
                    resolve(result)
                }
            }
        }

        @JvmStatic
        internal fun <V> allDone(on: TaggedExecutorService, promises: YSArray<XPromise<V>>): XPromise<YSArray<PromiseResult<V>>> {
            val items = promises.toList()
            val results = ConcurrentHashMap<Int, PromiseResult<V>>()
            val latch = CountDownLatch(items.size)

            return promise(on) { resolve, _ ->
                items.forEachIndexed { index, item ->
                    (item as Kromise<V>).finallyWithResult {
                        results[index] = it
                        latch.countDown()
                    }
                }

                latch.await()
                resolve((0 until items.size).map { results[it]!! })
            }
        }

        @JvmStatic
        val onUnhandledException = Event<YSError>()
    }
}

internal val DefaultExecutorService = TaggedExecutorService.Default(createSingleThreadExecutor("com.yandex.infra.DefaultExecutor"))
private val AwaitingExecutorService = TaggedExecutorService.Awaiting(Executors.newCachedThreadPool(Util.threadFactory("com.yandex.infra.AwaitingExecutor", true)))
private val DelayingExecutorService = TaggedExecutorService.Specialized("com.yandex.infra.DelayingExecutor", Executors.newCachedThreadPool(Util.threadFactory("com.yandex.infra.DelayingExecutorService", true)))

/**
 * Convenience method. Returns a promise which resolves when the
 * [Runnable] finishes. The Runnable is executed asynchronously.
 * If the Runnable throws, the returned promise rejects with the reason.
 *
 * @param block [Runnable] to execute.
 * @return XPromise<Unit>
 */
internal fun promise(on: TaggedExecutorService, block: Runnable): XPromise<Unit> =
    promise(on) { resolve, _ ->
        block.run()
        resolve(Unit)
    }

/**
 * Convenience method. Returns a promise which resolves when the
 * [Callable] finishes with the value returned by the Callable.
 * The Callable is executed asynchronously.  If the Callable throws,
 * the returned promise rejects with the reason.
 *
 * @param V type of the promise.
 * @param block [Callable] to execute.
 * @return XPromise<V>
 */
internal fun <V> promise(on: TaggedExecutorService, block: Callable<V>): XPromise<V> =
    promise(on) { resolve, _ ->
        resolve(block.call())
    }

/**
 * Returns a promise
 *
 * @param V Type of the promise's value
 *
 * @param executor A function that is passed with the arguments
 *    resolve and reject. The execService function is executed immediately,
 *    passing resolve and reject functions (the execService is called
 *    before the XPromise constructor even returns the created object).
 *
 *    The resolve and reject functions, when called, resolve or reject
 *    the promise, respectively. The execService normally initiates some
 *    asynchronous work, and then, once that completes, either calls
 *    the resolve function to resolve the promise or else rejects it
 *    if an error occurred.
 *
 *    If an error is thrown in the execService function, the promise
 *    is rejected.
 *
 */
fun <V> promise(executor: Executor<V>): XPromise<V> = promise(DefaultExecutorService, executor)
internal fun <V> promise(on: TaggedExecutorService, executor: Executor<V>): XPromise<V> = SettablePromise(on, executor)

fun <T> resolve(value: T): XPromise<T> = resolve(DefaultExecutorService, value)
internal fun <T> resolve(on: TaggedExecutorService, value: T): XPromise<T> = SettledPromise(on, value)

fun <T> reject(reason: YSError): XPromise<T> = reject(DefaultExecutorService, reason)
internal fun <T> reject(on: TaggedExecutorService, reason: YSError): XPromise<T> = SettledPromise(on, reason)

fun <T> all(promises: YSArray<XPromise<T>>): XPromise<YSArray<T>> = all(AwaitingExecutorService, promises)
internal fun <T> all(on: TaggedExecutorService, promises: YSArray<XPromise<T>>): XPromise<YSArray<T>> = Kromise.all(on, promises = promises)

fun <T> race(promises: YSArray<XPromise<T>>): XPromise<T> = race(AwaitingExecutorService, promises)
internal fun <T> race(on: TaggedExecutorService, promises: YSArray<XPromise<T>>): XPromise<T> = Kromise.race(on, promises)

fun <V> XPromise<V>.delay(msTime: Long): XPromise<V> = this.flatThen { delayed(it, msTime) }

fun <T> delayed(result: T, afterMs: Long): XPromise<T> = sleep(DelayingExecutorService, afterMs).then { result }

fun sleep(on: TaggedExecutorService, intervalMs: Long): XPromise<Unit> {
    val deferredPromise = deferred<Unit>(DefaultExecutorService)
    promise(on, Runnable {
        Thread.sleep(intervalMs)
        deferredPromise.resolve(Unit)
    })
    return deferredPromise.promise
}
