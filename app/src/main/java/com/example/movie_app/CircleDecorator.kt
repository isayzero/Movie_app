package com.example.movie_app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import android.text.style.LineBackgroundSpan

class CircleDecorator(
    context: Context
) : DayViewDecorator {

    private val paint: Paint = Paint()

    init {
        paint.color = ContextCompat.getColor(context, R.color.today)
        paint.style = Paint.Style.FILL
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        val today = CalendarDay.today()
        return day == today
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(object : LineBackgroundSpan {
            override fun drawBackground(
                canvas: Canvas,
                paint: Paint,
                left: Int,
                right: Int,
                top: Int,
                baseline: Int,
                bottom: Int,
                charSequence: CharSequence,
                start: Int,
                end: Int,
                lineNum: Int
            ) {
                val radius = (right - left) / 3f // 크기를 조정합니다.
                canvas.drawCircle(((left + right) / 2).toFloat(), (top + bottom) / 2f, radius, this@CircleDecorator.paint)
            }
        })
    }
}
