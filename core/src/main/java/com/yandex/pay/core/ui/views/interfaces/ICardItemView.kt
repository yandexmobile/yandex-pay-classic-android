package com.yandex.pay.core.ui.views.interfaces

internal interface ICardItemView : UpdatingView<ICardItemView> {
    enum class AccessoryType {
        None,
        Disclosure,
        Checkmark,
    }

    var loading: Boolean
    var accessoryType: AccessoryType
    var title: String
    var subtitle: String
    var image: Int?
    var onClick: () -> Unit
}

internal fun ICardItemView.from(other: ICardItemView, update: Boolean): ICardItemView = apply {
    loading = other.loading
    accessoryType = other.accessoryType
    title = other.title
    subtitle = other.subtitle
    image = other.image
    onClick = other.onClick
    if (update) {
        this.update()
    }
}
