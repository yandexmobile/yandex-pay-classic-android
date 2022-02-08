package com.yandex.pay.core.ui.views.interfaces

import android.text.method.MovementMethod

internal interface ILicenseAgreementView : View {
    var text: CharSequence
    var movementMethod: MovementMethod
}
