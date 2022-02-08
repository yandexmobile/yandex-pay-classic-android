package com.yandex.xplat.common

import java.util.concurrent.ExecutorService

sealed class BoundExecutor(val executor: TaggedExecutorService) : ExecutorService by executor {
    class OperationsExecutor(executor: TaggedExecutorService) : BoundExecutor(executor)
    class ResultsExecutor(executor: TaggedExecutorService = DefaultExecutorService) : BoundExecutor(executor)

    companion object {
        fun operationsExecutor(name: String) = OperationsExecutor(TaggedExecutorService.Specialized(name, createSingleThreadExecutor("com.yandex.infra.$name")))
    }
}

fun <T> asyncify(
    operationExecutor: BoundExecutor.OperationsExecutor,
    resultsExecutor: BoundExecutor.ResultsExecutor,
    invocation: () -> Result<T>
): XPromise<T> =
    toPromise(resultsExecutor.executor) { callback ->
        operationExecutor.submit {
            callback(invocation())
        }
    }
