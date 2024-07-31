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
        paint.color = ContextCompat.getColor(context, R.color.today)// 오늘 날짜를 나타내는 색상을 설정한다.
        paint.style = Paint.Style.FILL// 페인트 스타일을 채우기로 설정한다.
    }

    override fun shouldDecorate(day: CalendarDay): Boolean {
        val today = CalendarDay.today()
        return day == today// 오늘 날짜인 경우에만 데코레이션을 적용한다.
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
                val radius = (right - left) / 3f // 원의 반지름을 설정한다.
                // 캘린더 뷰의 중앙에 원을 그린다.
                canvas.drawCircle(((left + right) / 2).toFloat(), (top + bottom) / 2f, radius, this@CircleDecorator.paint)
            }
        })
    }
}
