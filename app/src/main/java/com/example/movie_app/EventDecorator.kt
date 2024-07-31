package com.example.movie_app

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.util.*

class EventDecorator(
    private val dates: Collection<CalendarDay>,
    context: Context
) : DayViewDecorator {

    private val color = ContextCompat.getColor(context, R.color.dot)// 점의 색상을 설정한다.

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)// 데코레이션을 적용할 날짜인지 확인한다.
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(15f, color))// DotSpan을 사용하여 날짜에 점을 추가한다.
    }
}
