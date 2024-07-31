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

    private val color = ContextCompat.getColor(context, R.color.dot)

    override fun shouldDecorate(day: CalendarDay): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(DotSpan(15f, color))
    }
}
