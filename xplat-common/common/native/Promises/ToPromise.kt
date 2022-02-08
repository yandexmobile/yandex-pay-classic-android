package com.yandex.xplat.common

private typealias ResultExecutor<T> = (/* result callback */ (Result<T>) -> Unit) -> Unit

fun <T> toPromise(resultsExecutor: TaggedExecutorService, operation: ResultExecutor<T>): XPromise<T> = promise(resultsExecutor) { resolve, reject ->
    operation { result ->
        if (result.isValue()) {
            resolve(result.getValue())
        } else {
            reject(result.getError())
        }
    }
}
