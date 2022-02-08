package com.yandex.xplat.common

/**
 * Discriminated union that captures the settlement result of
 * a [Kromise] (fulfillment or rejection)
 */
@Suppress("unused")
sealed class PromiseResult<out V> {

  /**
   * Value for fulfillment state
   */
  data class Value<out V>(val value: V) : PromiseResult<V>() {
    override fun toString(): String = "Value[$value]"
  }

  /**
   * Error for rejection state
   */
  class Error<out V>(val error: YSError) : PromiseResult<V>() {
    override fun toString(): String = "Error[${error.message}]"
  }
}
