package com.yandex.pay.core.ui.views.interfaces

internal interface ICheckoutButtonView : UpdatingView<ICheckoutButtonView> {
    enum class State {
        LOADING, CHECKING_OUT, CHECKED_OUT, NORMAL, ERROR,
    }

    var state: State
    var disabled: Boolean // `enabled` is a property of Android View so we call ours `disabled` to avoid collision
    var value: String
    var onClick: () -> Unit
}
