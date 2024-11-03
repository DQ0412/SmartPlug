package com.example.smartplugapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class LineChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint()
    private var dataPoints = listOf<Float>()

    init {
        paint.color = Color.BLUE
        paint.strokeWidth = 8f
        paint.style = Paint.Style.STROKE
    }

    fun setData(points: List<Float>) {
        dataPoints = points
        invalidate() // Yêu cầu vẽ lại
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (dataPoints.isEmpty()) return

        val width = width.toFloat()
        val height = height.toFloat()
        val maxDataPoint = dataPoints.maxOrNull() ?: return
        val minDataPoint = dataPoints.minOrNull() ?: return

        // Vẽ đường biểu đồ
        for (i in 0 until dataPoints.size - 1) {
            val startX = i * (width / (dataPoints.size - 1))
            val startY = height - ((dataPoints[i] - minDataPoint) / (maxDataPoint - minDataPoint) * height)
            val stopX = (i + 1) * (width / (dataPoints.size - 1))
            val stopY = height - ((dataPoints[i + 1] - minDataPoint) / (maxDataPoint - minDataPoint) * height)

            canvas.drawLine(startX, startY, stopX, stopY, paint)
        }
    }
}
