package com.example.seoulfesmap.ui.List

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.R
import com.example.seoulfesmap.RecyclerView.RecyclerAdapter
import com.example.seoulfesmap.RecyclerView.filterApdater
import com.example.seoulfesmap.databinding.FragmentDashboardBinding
import com.example.seoulfesmap.ui.calender.DayViewContainer
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.CalendarView
import com.kizitonwose.calendar.view.MonthDayBinder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class DashboardFragment : Fragment(), RecyclerAdapter.OnItemClickListener {

    private var selectedDate: LocalDate? = null
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var activityContext: Context

    private var list: ArrayList<FestivalData> = ArrayList()
    private var filterlist: ArrayList<String> = ArrayList()
    lateinit var adapter: RecyclerAdapter
    lateinit var filteradapter: filterApdater


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activityContext = requireActivity()
    }

    fun showCalendarDialog() {
        val dialogView = LayoutInflater.from(activityContext).inflate(R.layout.dialog_calendar, null)
        val daysOfWeek = daysOfWeek()

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth
        val endMonth = currentMonth.plusMonths(12)

        val monthTextView = dialogView.findViewById<TextView>(R.id.month)
        val calendarView = dialogView.findViewById<CalendarView>(R.id.calendarView)
        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                // Set the calendar day for this container.
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


        val alertDialog = AlertDialog.Builder(activityContext)
            .setView(dialogView)
            .create()

        alertDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun updateTitle(calender : CalendarView, monthText : TextView) {
        val month = calender.findFirstVisibleMonth()?.yearMonth ?: return
        monthText.text = month.year.toString() + " - " + month.monthValue.toString()
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecyclerView()

        return root
    }

    // dp를 픽셀로 변환하는 확장 함수

    fun showFesDataPopUp(position : Int) {
        val dialogView = LayoutInflater.from(activityContext).inflate(R.layout.fespopup, null)

        val alertDialog = AlertDialog.Builder(activityContext)
            .setView(dialogView)
            .create()

        alertDialog.show()
    }

    override fun OnItemClick(position: Int) {
        showFesDataPopUp(position)
    }

    fun initRecyclerView()
    {
        list.add(FestivalData("https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=43bd8ae3612e4cb2bb3a7edf9186efbf&thumb=Y", "아무튼 신나는 축제", "건국대 앞", LocalDateTime.of(2023, 10, 20, 9, 0) , LocalDateTime.of(2023, 11, 10, 21, 0)))
        list.add(FestivalData("https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=d5e5494491b1481081180ac991c410db&thumb=Y","아무튼 신나는 축제 2", "건국대 뒤", LocalDateTime.of(2023, 12, 25, 9, 0) , LocalDateTime.of(2023, 12, 31, 21, 0)))

        filterlist.add("전체")
        filterlist.add("콘서트")
        filterlist.add("전통문화")
        filterlist.add("뮤지컬")
        filterlist.add("뮤지컬1")
        filterlist.add("뮤지컬1")
        filterlist.add("뮤지컬1")

        adapter = RecyclerAdapter(list)
        filteradapter = filterApdater(filterlist)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val dividerDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerDecoration)
        adapter.itemClickListener = this
        binding.recyclerView.adapter = adapter


        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.filterRecycle.setLayoutManager(layoutManager)
        binding.filterRecycle.adapter = filteradapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.list_menu_button1-> {
                Log.d("Calender", "Calender");
                showCalendarDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}