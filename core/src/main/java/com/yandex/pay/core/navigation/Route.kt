package com.yandex.pay.core.navigation

import android.net.Uri

internal sealed class Route(
    val name: String,
    val animated: Boolean,
) {
    object Authorization : Route("AUTHORIZATION", false)
    object GetCards : Route("GET_CARDS", false)
    object SelectCard : Route("SELECT_CARD", true)
    object NewCardNumberBinding : Route("NEW_CARD_NUMBER_BINDING", true)
    class Confirm3DS(val uri: Uri) : Route("CONFIRM_3DS", true)
}
