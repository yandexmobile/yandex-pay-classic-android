package com.yandex.pay.core.ui.views.interfaces

internal interface IBindCardButtonView : UpdatingView<IBindCardButtonView> {
    enum class State {
        PROGRESS, DONE, NORMAL, ERROR
    }

    var state: State
    var disabled: Boolean // `enabled` is a property of Android View so we call ours `disabled` to avoid collision
    var title: String
    var onClick: () -> Unit
}
