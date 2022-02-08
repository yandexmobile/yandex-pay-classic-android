package com.yandex.pay.core.ui.views.interfaces

internal interface Presenter<Data, V: View> {
    fun present(data: Data, view: V)
}
