package me.huizengek.snpack.stickers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.graphics.withTranslation
import me.huizengek.snpack.util.useAsInput
import kotlin.math.roundToInt

private fun createSticker(width: Int, aspect: Float, draw: Canvas.() -> Unit) =
    createSticker(width, (width * aspect).roundToInt(), draw)

private fun createSticker(width: Int, height: Int, draw: Canvas.() -> Unit): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    canvas.draw()

    return bitmap
}

fun Canvas.drawText(
    text: String,
    size: Float,
    stroke: Float,
    fillColor: Int = Color.WHITE,
    strokeColor: Int = Color.BLACK
) {
    val paint = TextPaint().apply {
        textAlign = Paint.Align.CENTER
        color = strokeColor
        style = Paint.Style.FILL_AND_STROKE
        textSize = size
        strokeWidth = stroke
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isFakeBoldText = true
        isAntiAlias = true
    }

    val actualText = text.trim()

    val layout = StaticLayout.Builder
        .obtain(actualText, 0, actualText.length, paint, (width * 0.8).roundToInt())
        .build()

    withTranslation(
        layout.width / 1.6f,
        height / 2f - layout.height / 2f
    ) {
        layout.draw(this)
        paint.apply {
            style = Paint.Style.FILL
            color = fillColor
        }
        layout.draw(this)
    }
}

context(Context)
fun String.toSticker(
    size: Float,
    stroke: Float,
    fillColor: Int = Color.WHITE,
    strokeColor: Int = Color.BLACK,
    width: Int = 512,
    aspect: Float = 1.0f
) = createSticker(width, aspect) {
    drawText(
        text = this@toSticker,
        size = size,
        stroke = stroke,
        fillColor = fillColor,
        strokeColor = strokeColor
    )
}

context(Context)
fun Uri.toSticker(
    width: Int = 512,
    aspect: Float = 1.0f
): Bitmap? {
    val bitmap = useAsInput("image/*") { BitmapFactory.decodeStream(it) }
        ?: return null

    return createSticker(width, aspect) {
        val matrix = Matrix()
        matrix.setRectToRect(
            /* src = */ RectF(0f, 0f, bitmap.width.toFloat(), bitmap.height.toFloat()),
            /* dst = */ RectF(0f, 0f, this.width.toFloat(), height.toFloat()),
            /* stf = */ Matrix.ScaleToFit.CENTER
        )
        save()
        concat(matrix)
        drawBitmap(bitmap, 0f, 0f, null)
        restore()
    }.also { bitmap.recycle() }
}
