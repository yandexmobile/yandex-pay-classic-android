package com.yandex.pay.core.ui.views.interfaces

internal interface IHeaderView : UpdatingView<IHeaderView> {
    var root: Boolean
    val avatar: IAvatarView
    var onBackButtonClick: (() -> Unit)?
}
