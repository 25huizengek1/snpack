package me.huizengek.snpack.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import me.huizengek.snpack.stickers.drawText
import me.huizengek.snpack.util.toPx
import kotlin.math.roundToInt

private data class StickerPreviewState(
    val text: String = "",
    val size: Float = 16f,
    val stroke: Float = .5f,
    val fillColor: Int = Color.WHITE,
    val strokeColor: Int = Color.BLACK
)

private class StickerPreviewView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    exactWidth: Int = -1,
    exactHeight: Int = -1
) : View(context, attrs, defStyleAttr) {
    var stickerPreviewState = StickerPreviewState()
        set(value) {
            field = value
            invalidate()
        }

    @get:JvmName("getWidth1")
    var width = exactWidth
        set(value) {
            field = value
            invalidate()
        }

    @get:JvmName("getHeight1")
    var height = exactHeight
        set(value) {
            field = value
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.clipRect(0, 0, width, height)

        canvas.drawText(
            text = stickerPreviewState.text,
            size = stickerPreviewState.size,
            stroke = stickerPreviewState.stroke,
            fillColor = stickerPreviewState.fillColor,
            strokeColor = stickerPreviewState.strokeColor
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = if (width < 1) widthMeasureSpec else MeasureSpec.makeMeasureSpec(
            width,
            MeasureSpec.EXACTLY
        )
        val measuredHeight = if (height < 1) heightMeasureSpec else MeasureSpec.makeMeasureSpec(
            height,
            MeasureSpec.EXACTLY
        )
        setMeasuredDimension(measuredWidth, measuredHeight)
    }
}

@Composable
fun StickerPreview(
    text: String,
    size: Float,
    stroke: Float,
    fillColor: Int,
    strokeColor: Int,
    modifier: Modifier = Modifier
) = BoxWithConstraints(modifier = modifier) {
    val width = maxWidth.toPx()
    val height = maxHeight.toPx()

    AndroidView(
        factory = { context ->
            StickerPreviewView(
                context = context,
                exactWidth = width.roundToInt(),
                exactHeight = height.roundToInt()
            )
        },
        update = {
            it.stickerPreviewState = StickerPreviewState(
                text = text,
                size = size,
                stroke = stroke,
                fillColor = fillColor,
                strokeColor = strokeColor
            )
        },
        modifier = Modifier.size(width = maxWidth, height = maxHeight)
    )
}