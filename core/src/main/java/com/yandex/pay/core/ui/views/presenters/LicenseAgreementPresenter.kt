package com.yandex.pay.core.ui.views.presenters

import android.content.res.Resources
import android.text.*
import android.text.Annotation
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.yandex.pay.core.R
import com.yandex.pay.core.ui.views.interfaces.ILicenseAgreementView
import com.yandex.pay.core.ui.views.interfaces.Presenter

internal class LicenseAgreementPresenter(
    private val resources: Resources,
    private val theme: Resources.Theme,
    private val annotationKey: String,
) : Presenter<LicenseAgreementPresenter.Payload, ILicenseAgreementView> {
    class Payload(@StringRes val textId: Int, val onClick: () -> Unit)

    override fun present(data: Payload, view: ILicenseAgreementView) {
        val text = resources.getText(data.textId) as SpannedString
        val spannable = SpannableString(text)
        val annotations = text.getSpans(0, text.length, Annotation::class.java)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                data.onClick()
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = false
            }
        }

        annotations?.find { it.value == annotationKey }?.let { annotation ->
            spannable.apply {
                setSpan(
                    clickableSpan,
                    text.getSpanStart(annotation),
                    text.getSpanEnd(annotation),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
                setSpan(
                    ForegroundColorSpan(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.yandexpay_license_agreement_link,
                            theme
                        )
                    ),
                    text.getSpanStart(annotation),
                    text.getSpanEnd(annotation),
                    0
                )
            }
        }

        view.text = spannable
        view.movementMethod = LinkMovementMethod.getInstance()
    }
}
