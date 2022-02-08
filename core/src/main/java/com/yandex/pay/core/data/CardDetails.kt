package com.yandex.pay.core.data

import android.os.Parcelable
import androidx.annotation.IntRange
import com.yandex.xplat.yandex.pay.BankName
import com.yandex.xplat.yandex.pay.NewCard
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class CardDetails(
    val number: String,
    @IntRange(from = 1, to = 12) val expirationMonth: Int?,
    @IntRange(from = 0, to = 99) val expirationYear: Int?,
    val cvn: String,
) : Parcelable {
    internal companion object {
        val empty: CardDetails = CardDetails("", null, null, "")
    }

    val isEmpty: Boolean
        get() = this == empty

    val isPartiallyFilled: Boolean get() = number.isNotBlank()

    val isCompletelyFilled: Boolean
        get() = isPartiallyFilled && expirationMonth != null && expirationYear != null && cvn.isNotBlank()

    fun toNewCard(
        shouldBeStored: Boolean = true,
        bankName: BankName = BankName.UnknownBank
    ): NewCard = NewCard(
        number,
        expirationMonth.toString(),
        expirationYear.toString(),
        cvn,
        shouldBeStored,
        bankName,
    )
}
