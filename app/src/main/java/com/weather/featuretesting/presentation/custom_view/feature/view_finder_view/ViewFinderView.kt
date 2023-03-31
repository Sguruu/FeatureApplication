package com.weather.featuretesting.presentation.custom_view.feature.view_finder_view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.annotation.Px
import com.weather.featuretesting.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val DEFAULT_MASK_COLOR = 0x77000000
private const val DEFAULT_FRAME_COLOR = Color.WHITE
private const val DEFAULT_FRAME_THICKNESS_DP = 2f
private const val DEFAULT_FRAME_ASPECT_RATIO_WIDTH = 1f
private const val DEFAULT_FRAME_ASPECT_RATIO_HEIGHT = 1f
private const val DEFAULT_FRAME_CORNER_SIZE_DP = 50f
private const val DEFAULT_FRAME_CORNERS_RADIUS_DP = 0f
private const val DEFAULT_FRAME_SIZE = 0.75f
private const val DEFAULT_INDENT_LINES_FRAME_DP = 0f

/**
 * Отображает рамку для сканирования qr-кода
 *
 * Данный view-компонент был взят с библиотеки:
 * https://github.com/yuriy-budiyev/code-scanner
 *
 * Исходный код данного класа:
 * https://github.com/yuriy-budiyev/code-scanner/blob/master/src/main/java/com/budiyev/android/codescanner/CodeScannerView.java
 *
 * **/
class ViewFinderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    lateinit var frameRect: Rect

    private lateinit var maskPaint: Paint
    private lateinit var framePaint: Paint
    private lateinit var path: Path

    private var size = 0f
    private var ratioHeight = 0f
    private var ratioWidth = 0f

    init {
        setupPaint()
        setupPath()

        applyAttrs(attrs)
    }

    private fun setupPaint() {
        maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }
        framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
        }
    }

    private fun setupPath() {
        path = Path().apply {
            fillType = Path.FillType.EVEN_ODD
        }
    }

    private fun applyAttrs(attrs: AttributeSet?) {
        val density = context.resources.displayMetrics.density
        if (attrs == null) {
            setFrameAspectRatio(DEFAULT_FRAME_ASPECT_RATIO_WIDTH, DEFAULT_FRAME_ASPECT_RATIO_HEIGHT)
            maskColor = DEFAULT_MASK_COLOR
            frameColor = DEFAULT_FRAME_COLOR
            frameThickness = (DEFAULT_FRAME_THICKNESS_DP * density).roundToInt()
            frameCornersSize = (DEFAULT_FRAME_CORNER_SIZE_DP * density).roundToInt()
            frameCornersRadius = (DEFAULT_FRAME_CORNERS_RADIUS_DP * density).roundToInt()
            frameSize = DEFAULT_FRAME_SIZE
            indentLinesFrame = (DEFAULT_INDENT_LINES_FRAME_DP * density).roundToInt()
        } else {
            var a: TypedArray? = null
            try {
                a = context.theme.obtainStyledAttributes(attrs, R.styleable.ViewFinderView, 0, 0)
                setFrameAspectRatio(
                    a.getFloat(R.styleable.ViewFinderView_frameAspectRatioWidth, DEFAULT_FRAME_ASPECT_RATIO_WIDTH),
                    a.getFloat(R.styleable.ViewFinderView_frameAspectRatioHeight, DEFAULT_FRAME_ASPECT_RATIO_HEIGHT)
                )
                maskColor = a.getColor(R.styleable.ViewFinderView_maskColor, DEFAULT_MASK_COLOR)
                frameColor = a.getColor(R.styleable.ViewFinderView_frameColor, DEFAULT_FRAME_COLOR)
                frameThickness = a.getDimensionPixelOffset(
                    R.styleable.ViewFinderView_frameThickness,
                    (DEFAULT_FRAME_THICKNESS_DP * density).roundToInt()
                )
                frameCornersSize = a.getDimensionPixelOffset(
                    R.styleable.ViewFinderView_frameCornersSize,
                    (DEFAULT_FRAME_CORNER_SIZE_DP * density).roundToInt()
                )
                frameCornersRadius = a.getDimensionPixelOffset(
                    R.styleable.ViewFinderView_frameCornersRadius,
                    (DEFAULT_FRAME_CORNERS_RADIUS_DP * density).roundToInt()
                )
                frameSize = a.getFloat(R.styleable.ViewFinderView_frameSize, DEFAULT_FRAME_SIZE)
                indentLinesFrame =
                    a.getDimensionPixelOffset(
                        R.styleable.ViewFinderView_indentLinesFrame,
                        (DEFAULT_INDENT_LINES_FRAME_DP * density).roundToInt()
                    )
            } finally {
                a?.recycle()
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        invalidateFrameRect(right - left, bottom - top)
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        val top = frameRect.top.toFloat()
        val left = frameRect.left.toFloat()
        val right = frameRect.right.toFloat()
        val bottom = frameRect.bottom.toFloat()
        val frameCornersSize = frameCornersSize.toFloat()
        val frameCornersRadius = frameCornersRadius.toFloat()
        //    val frameCornersRadius = 0

        if (frameCornersRadius > 0) {
            // отвечает за закругление
            val normalizedRadius = min(frameCornersRadius, max(frameCornersSize - 1, 0f))

            path.reset()
            path.moveTo(left, top + normalizedRadius)

            path.quadTo(left, top, left + normalizedRadius, top)
            path.lineTo(right - normalizedRadius, top)

            path.quadTo(right, top, right, top + normalizedRadius)
            path.lineTo(right, bottom - normalizedRadius)

            path.quadTo(right, bottom, right - normalizedRadius, bottom)
            path.lineTo(left + normalizedRadius, bottom)

            // углы рамки
            path.quadTo(left, bottom, left, bottom - normalizedRadius)
            path.lineTo(left, top + normalizedRadius)

            // отвечает за фон вокруг рамки не включая рамку
            path.moveTo(0f, 0f)
            path.lineTo(width, 0f)
            path.lineTo(width, height)
            path.lineTo(0f, height)
            path.lineTo(0f, 0f)
            // *

            canvas.drawPath(path, maskPaint)

            path.reset()
            // начальная точка контура
            path.moveTo(left - indentLinesFrame, top + frameCornersSize)
            // линия от послденй точки
            path.lineTo(left - indentLinesFrame, top + normalizedRadius)
            // базье
            path.quadTo(
                left - indentLinesFrame,
                top - indentLinesFrame,
                left + normalizedRadius,
                top - indentLinesFrame
            )
            path.lineTo(left + frameCornersSize, top - indentLinesFrame)

            path.moveTo(right - frameCornersSize, top - indentLinesFrame)
            path.lineTo(right - normalizedRadius, top - indentLinesFrame)
            path.quadTo(
                right + indentLinesFrame,
                top - indentLinesFrame,
                right + indentLinesFrame,
                top + normalizedRadius
            )
            path.lineTo(right + indentLinesFrame, top + frameCornersSize)

            path.moveTo(right + indentLinesFrame, bottom - frameCornersSize)
            path.lineTo(right + indentLinesFrame, bottom - normalizedRadius)
            path.quadTo(
                right + indentLinesFrame,
                bottom + indentLinesFrame,
                right - normalizedRadius,
                bottom + indentLinesFrame
            )
            path.lineTo(right - frameCornersSize, bottom + indentLinesFrame)

            path.moveTo(left + frameCornersSize, bottom + indentLinesFrame)
            path.lineTo(left + normalizedRadius, bottom + indentLinesFrame)
            path.quadTo(
                left - indentLinesFrame,
                bottom + indentLinesFrame,
                left - indentLinesFrame,
                bottom - normalizedRadius
            )
            path.lineTo(left - indentLinesFrame, bottom - frameCornersSize)

            canvas.drawPath(path, framePaint)
        } else {
            path.reset()
            path.moveTo(left, top)
            path.lineTo(right, top)
            path.lineTo(right, bottom)
            path.lineTo(left, bottom)
            path.lineTo(left, top)

            path.moveTo(0f, 0f)
            path.lineTo(width, 0f)
            path.lineTo(width, height)
            path.lineTo(0f, height)
            path.lineTo(0f, 0f)
            canvas.drawPath(path, maskPaint)

            path.reset()
            path.moveTo(left - indentLinesFrame, top + frameCornersSize)
            path.lineTo(left - indentLinesFrame, top - indentLinesFrame)
            path.lineTo(left + frameCornersSize, top - indentLinesFrame)

            path.moveTo(right - frameCornersSize, top - indentLinesFrame)
            path.lineTo(right + indentLinesFrame, top - indentLinesFrame)
            path.lineTo(right + indentLinesFrame, top + frameCornersSize)

            path.moveTo(right + indentLinesFrame, bottom - frameCornersSize)
            path.lineTo(right + indentLinesFrame, bottom + indentLinesFrame)
            path.lineTo(right - frameCornersSize, bottom + indentLinesFrame)

            path.moveTo(left + frameCornersSize, bottom + indentLinesFrame)
            path.lineTo(left - indentLinesFrame, bottom + indentLinesFrame)
            path.lineTo(left - indentLinesFrame, bottom - frameCornersSize)
            canvas.drawPath(path, framePaint)
        }
    }

    fun setFrameAspectRatio(
        @FloatRange(from = 0.0, fromInclusive = false) ratioWidth: Float,
        @FloatRange(from = 0.0, fromInclusive = false) ratioHeight: Float
    ) {
        this.ratioWidth = ratioWidth
        this.ratioHeight = ratioHeight
        invalidateFrameRect()
        if (isLaidOut) {
            invalidate()
        }
    }

    @get:ColorInt
    var maskColor: Int
        get() = maskPaint.color
        set(color) {
            maskPaint.color = color
            if (isLaidOut) {
                invalidate()
            }
        }

    @get:ColorInt
    var frameColor: Int
        get() = framePaint.color
        set(color) {
            framePaint.color = color
            if (isLaidOut) {
                invalidate()
            }
        }

    @get:Px
    var frameThickness: Int
        get() = framePaint.strokeWidth.toInt()
        set(thickness) {
            framePaint.strokeWidth = thickness.toFloat()
            if (isLaidOut) {
                invalidate()
            }
        }

    @get:Px
    var frameCornersSize: Int = 0
        set(size) {
            field = size
            if (isLaidOut) {
                invalidate()
            }
        }

    @get:Px
    var frameCornersRadius: Int = 0
        set(radius) {
            field = radius
            if (isLaidOut) {
                invalidate()
            }
        }

    @get:FloatRange(from = 0.1, to = 1.0)
    var frameSize: Float
        get() = size
        set(size) {
            this.size = size
            invalidateFrameRect()
            if (isLaidOut) {
                invalidate()
            }
        }

    @get:Px
    var indentLinesFrame: Int = 0
        set(indent) {
            field = indent
            if (isLaidOut) {
                invalidate()
            }
        }

    private fun invalidateFrameRect(width: Int = getWidth(), height: Int = getHeight()) {
        if (width > 0 && height > 0) {
            val viewAR = width.toFloat() / height.toFloat()
            val frameAR = ratioWidth / ratioHeight
            val frameWidth: Int
            val frameHeight: Int
            if (viewAR <= frameAR) {
                frameWidth = (width * size).roundToInt()
                frameHeight = (frameWidth / frameAR).roundToInt()
            } else {
                frameHeight = (height * size).roundToInt()
                frameWidth = (frameHeight * frameAR).roundToInt()
            }
            val frameLeft = (width - frameWidth) / 2
            val frameTop = (height - frameHeight) / 2

            frameRect = Rect(frameLeft, frameTop, frameLeft + frameWidth, frameTop + frameHeight)
        }
    }
}
