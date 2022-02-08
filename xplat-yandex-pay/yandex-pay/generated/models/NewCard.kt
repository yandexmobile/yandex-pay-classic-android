// <<< AUTOGENERATED BY YANDEX.SCRIPT FROM models/new-card.ts >>>

package com.yandex.xplat.yandex.pay

import com.yandex.xplat.common.*
import com.yandex.xplat.eventus.common.*

public open class NewCard(val cardNumber: String, val expirationMonth: String, val expirationYear: String, val cvn: String, val shouldBeStored: Boolean, val bank: BankName = BankName.UnknownBank) {
}
