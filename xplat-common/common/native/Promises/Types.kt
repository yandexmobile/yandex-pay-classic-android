package com.yandex.xplat.common

import android.util.Log
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import okhttp3.internal.Util

typealias Executor<V> = XPromise<V>.((V) -> Unit, (YSError) -> Unit) -> Unit

fun buildFailure(message: Throwable) = YSError("Failure from Throwable: $message\nUnderlying stack trace: ${Log.getStackTraceString(message)}")

fun Throwable.toFailure(): YSError = buildFailure(this)

fun createSingleThreadExecutor(name: String): ExecutorService = Executors.newSingleThreadExecutor(Util.threadFactory(name, true))
