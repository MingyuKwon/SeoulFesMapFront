package com.example.seoulfesmap.ui.Popup

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.example.seoulfesmap.R
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.YearMonth

interface CalendarDialogListener {
    fun onDateSelected(startDate: String?, endDate: String?)
}

class CalendarDialogFragment (var startFilter : String? ,
                              var endFilter : String?): DialogFragment() {

    var calendarDialogListener: CalendarDialogListener? = null

    class DayViewContainer(view: View) : ViewContainer(view) {
        val textView = view.findViewById<TextView>(R.id.calendarDayText)
        // Will be set when this container is bound
        lateinit var day: CalendarDay
        init {
            view.setOnClickListener {
                if(mode  == 1)
                {
                    startText!!.text = day.date.toString()
                    changeViewBorderColor(startText!!, R.color.black)
                    changeViewBorderColor(endText!!, R.color.purple_500)

                    mode = 2
                }else if(mode  == 2)
                {
                    endText!!.text = day.date.toString()
                    mode = 0
                    filterButton!!.setBackgroundColor(ContextCompat.getColor(view.context, R.color.purple_500))
                    filterButton!!.isEnabled = true
                    changeViewBorderColor(startText!!, R.color.black)
                    changeViewBorderColor(endText!!, R.color.black)


                }

            }
        }
    }

    companion object {
        // 이곳에 정적(static-like) 변수와 메소드를 정의할 수 있습니다.
        var mode = 0 // 1은 start 입력, 2는 end 입력, 0은 기본
        var startText : TextView? = null
        var endText : TextView? = null
        var filterButton : Button? = null

        fun changeViewBorderColor(view: View, newColor: Int) {
            val background = view.background
            var borderWidth = 10
            if(newColor != R.color.black) borderWidth = 15

            if (background is GradientDrawable) {
                background.setStroke(borderWidth, newColor) // borderWidth는 테두리의 두께입니다.
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        mode = 0
        startText = null
        endText = null
        filterButton = null

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 프래그먼트에 대한 레이아웃을 인플레이트합니다.

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_calendar, null)

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth
        val endMonth = currentMonth.plusMonths(12)
        val daysOfWeek = daysOfWeek() // 이 함수가 다른 곳에서 정의되어 있다고 가정합니다.

        val monthTextView = dialogView.findViewById<TextView>(R.id.month)
        val calendarView = dialogView.findViewById<CalendarView>(R.id.calendarView)

        startText = dialogView.findViewById<TextView>(R.id.DateStartText)
        endText = dialogView.findViewById<TextView>(R.id.DateEndText)
        filterButton = dialogView.findViewById<Button>(R.id.FilterStartButton)

        changeViewBorderColor(startText!!, R.color.black)
        changeViewBorderColor(endText!!, R.color.black)


        filterButton!!.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_500))


        var filterClearButton = dialogView.findViewById<Button>(R.id.FilterClearButton)


        val closeButton = dialogView.findViewById<Button>(R.id.CLoseButton)

        startText!!.text = startFilter
        endText!!.text = endFilter

        filterButton!!.setOnClickListener {
            mode = 1 // 시작 날짜를 선택하는 모드로 설정
            filterButton!!.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_200))
            filterButton!!.isEnabled = false
            changeViewBorderColor(startText!!, R.color.purple_500)
            changeViewBorderColor(endText!!, R.color.black)

        }
        
        filterClearButton.setOnClickListener {
            startText!!.text = ""
            endText!!.text = ""

        }

        closeButton.setOnClickListener {
            calendarDialogListener!!.onDateSelected(startText!!.text.toString(), endText!!.text.toString())
            dismiss()
        }


        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
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