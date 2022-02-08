// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM result/result.ts >>>

package com.yandex.xplat.common


public fun getVoid(): Unit {
}

public open class Result<T>(value: T?, error: YSError?) {
    private val boxedValue: Box<T>?
    private val error: YSError?
    init {
        this.boxedValue = if (error == null) Box<T>(value as T) else null
        this.error = error
    }
    open fun isValue(): Boolean {
        return this.error == null
    }

    open fun isError(): Boolean {
        return this.error != null
    }

    open fun getValue(): T {
        return nullthrows(this.boxedValue).value
    }

    open fun getError(): YSError {
        return nullthrows(this.error)
    }

    open fun tryGetValue(): T {
        if (this.isError()) {
            throw this.getError()
        }
        return this.getValue()
    }

    open fun <U> withValue(f: (T) -> U): U? {
        if (this.isValue()) {
            return f(this.getValue())
        }
        return null
    }

    open fun <U> map(f: (T) -> U): Result<U> {
        if (this.isValue()) {
            return Result<U>(f(this.getValue()), null)
        }
        return Result<U>(null, this.getError())
    }

    open fun <U> flatMap(f: (T) -> Result<U>): Result<U> {
        if (this.isValue()) {
            return f(this.getValue())
        }
        return Result<U>(null, this.getError())
    }

    open fun asNullable(): T? {
        return if (this.isError()) null else this.getValue()
    }

}

public fun <T> resultValue(value: T): Result<T> {
    return Result<T>(value, null)
}

public fun <T> resultError(error: YSError): Result<T> {
    return Result<T>(null, error)
}

