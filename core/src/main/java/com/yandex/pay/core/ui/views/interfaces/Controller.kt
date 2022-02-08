package com.yandex.pay.core.ui.views.interfaces

internal interface Controller<Data, V: View> {
    fun bind(data: Data, view: V)
    fun unbind(view: V)
}
