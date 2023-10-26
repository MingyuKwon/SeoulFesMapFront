package com.example.seoulfesmap.ui.calender

import android.util.Log
import android.view.View
import android.widget.TextView
import com.example.seoulfesmap.R
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.calendarDayText)
    // Will be set when this container is bound
    lateinit var day: CalendarDay

    init {
        view.setOnClickListener {
            Log.d("DayViewContainer", day.date.toString());
        }
    }
}