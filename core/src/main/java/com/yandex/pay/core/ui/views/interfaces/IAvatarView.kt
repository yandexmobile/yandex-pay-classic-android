package com.yandex.pay.core.ui.views.interfaces

import android.graphics.drawable.Drawable

internal interface IAvatarView : UpdatingView<IAvatarView> {
    var name: String
    var image: Drawable?
    var onClick: () -> Unit
    var disabled: Boolean
}
