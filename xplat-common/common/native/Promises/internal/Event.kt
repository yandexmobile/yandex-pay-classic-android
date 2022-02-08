package com.yandex.xplat.common

typealias EventHandler<T> = (T?) -> Unit

class Event<T> {
  private val handlers = mutableSetOf<EventHandler<T>>()
  private var hasSticky = false
  private var sticky: T? = null

  operator fun plusAssign(handler: EventHandler<T>) {
    synchronized(handlers) {
      handlers.add(handler)
    }
    if (hasSticky) {
      handler(sticky)
    }
  }

  operator fun minusAssign(handler: EventHandler<T>) {
    synchronized(handlers) {
      handlers.remove(handler)
    }
  }

  operator fun invoke(value: T? = null, sticky: Boolean = false) {
    if (sticky) {
      this.sticky = value
      hasSticky = true
    }
    handlers.toSet().forEach { it(value) }
  }
}
