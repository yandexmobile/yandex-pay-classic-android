package com.yandex.pay.core.ui.views.interfaces

// Must be used with CRTP (https://en.wikipedia.org/wiki/Curiously_recurring_template_pattern)
internal interface UpdatingView<T : View> : View {
    fun update()
}

internal inline fun <reified T : View> UpdatingView<T>.updating(block: T.() -> Unit) {
    if (this !is T) {
        throw IllegalStateException("Updating can be called only on objects implementing CRTP with UpdatingView")
    }
    block(this)
    update()
}
