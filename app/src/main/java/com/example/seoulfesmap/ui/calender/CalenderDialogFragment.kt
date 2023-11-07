package com.example.seoulfesmap.ui.calender

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.seoulfesmap.R
import com.example.seoulfesmap.ui.List.DashboardFragment
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.YearMonth

class CalendarDialogFragment : DialogFragment() {

    // 필요한 경우 생성자나 팩토리 메서드를 통해 파라미터를 전달할 수도 있습니다.

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 프래그먼트에 대한 레이아웃을 인플레이트합니다.
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_calendar, null)

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth
        val endMonth = currentMonth.plusMonths(12)
        val daysOfWeek = daysOfWeek() // 이 함수가 다른 곳에서 정의되어 있다고 가정합니다.

        val monthTextView = dialogView.findViewById<TextView>(R.id.month)
        val calendarView = dialogView.findViewById<CalendarView>(R.id.calendarView)

        calendarView.dayBinder = object : MonthDayBinder<DashboardFragment.DayViewContainer> {
            override fun create(view: View) = DashboardFragment.DayViewContainer(view)
            override fun bind(container: DashboardFragment.DayViewContainer, data: CalendarDay) {
                container.day = data
                // Set the date text
                container.textView.text = data.date.dayOfMonth.toString()
                // Any other binding logic
                if (data.position == DayPosition.MonthDate) {
                    container.textView.setTextColor(Color.BLACK)
                } else {
                    container.textView.setTextColor(Color.GRAY)
                }
            }
        }

        calendarView.monthScrollListener = { updateTitle(calendarView, monthTextView) }
        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        return AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

    }

    private fun updateTitle(calender : CalendarView, monthText : TextView) {
        val month = calender.findFirstVisibleMonth()?.yearMonth ?: return
        monthText.text = month.year.toString() + " - " + month.monthValue.toString()
    }

    // 필요한 추가 함수를 추가합니다.
}