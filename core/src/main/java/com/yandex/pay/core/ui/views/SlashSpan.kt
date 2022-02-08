package com.yandex.pay.core.ui.views

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan

internal class SlashSpan : ReplacementSpan() {
    private val sb = StringBuilder()

    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val padding = paint.measureText(PADDING, 0, 1)
        val separator = paint.measureText(SEPARATOR, 0, 1)
        val textSize = paint.measureText(text, start, end)
        return ((padding + separator + padding) + textSize).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        sb.setLength(0)
        sb.append(text.subSequence(start, end)).append(PADDING + SEPARATOR + PADDING)
        canvas.drawText(sb, 0, sb.length, x, y.toFloat(), paint)
    }

    private companion object {
        const val SEPARATOR: String = "/"
        const val PADDING: String = " "
    }
}
