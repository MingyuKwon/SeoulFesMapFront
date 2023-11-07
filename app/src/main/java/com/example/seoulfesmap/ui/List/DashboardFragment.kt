package com.example.seoulfesmap.ui.List

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
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
import okhttp3.internal.notify
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

    fun showFesDataPopUp(fesData : FestivalData) {
        val dialogView = LayoutInflater.from(activityContext).inflate(R.layout.fespopup, null)

        val alertDialog = AlertDialog.Builder(activityContext)
            .setView(dialogView)
            .create()

        val fesImage : ImageView = dialogView.findViewById(R.id.imageView)

        val titleText : TextView = dialogView.findViewById(R.id.FesTitle)
        val locationText : TextView = dialogView.findViewById(R.id.FestLocation)
        val dateText : TextView = dialogView.findViewById(R.id.FesDate)

        val detailbutton: Button = dialogView.findViewById(R.id.button1)
        val communitybutton: Button = dialogView.findViewById(R.id.button2)
        val closebutton: Button = dialogView.findViewById(R.id.button3)


        Glide.with(activityContext)
            .load(fesData.imageResourceUrl)
            .into(fesImage)

        titleText.text = fesData.FesTitle
        locationText.text = fesData.FesLocation
        dateText.text = fesData.FesStartDate!!.toLocalDate().toString()

        detailbutton.setOnClickListener {
            alertDialog.dismiss()
        }
        communitybutton.setOnClickListener {
            alertDialog.dismiss()
        }
        closebutton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()

        val window = alertDialog.window
        if (window != null) {
            val size = Point()
            val display = window.windowManager.defaultDisplay
            display.getSize(size)
            // 화면 너비의 일정 비율로 팝업 크기를 설정할 수 있습니다.
            window.setLayout((size.x * 1).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun OnItemClick(position: Int) {
        showFesDataPopUp(adapter.filteredList[position])
    }

    fun initRecyclerView()
    {
        list.add(FestivalData(8037, "콘서트","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=43bd8ae3612e4cb2bb3a7edf9186efbf&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143909&menuNo=200008",
            "마포아트센터 M 레트로 시리즈 2024 신년맞이 어떤가요 #7", "마포아트센터 아트홀 맥", "2024-01-18T00:00:00.000Z" , "2024-01-18T00:00:00.000Z", "37.5499060881738", "126.945533810385"))
        list.add(FestivalData(8038,"콘서트","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=d5e5494491b1481081180ac991c410db&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143406&menuNo=200008",
            "딕펑스×두번째달_Spice of life", "꿈의숲 퍼포먼스홀", "2023-12-23T00:00:00.000Z" , "2023-12-23T00:00:00.000Z", "37.6202544613023", "127.044324732036"))
        list.add(FestivalData(8039, "전시/미술","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=cc68500bcc0a4e0f89143a5a89d5facb&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143763&menuNo=200009",
            "서울일러스트레이션페어V.16", "코엑스 B&D1홀", "2023-12-21T00:00:00.000Z", "2023-12-24T00:00:00.000Z", "37.5103947", "127.0611127"))


        val uniqueCategories = list.map { it.category }.toSet().filterNotNull()
        val uniqueCategoriesList = ArrayList(uniqueCategories)
        filterlist.add("전체")
        filterlist.addAll(uniqueCategoriesList)

        adapter = RecyclerAdapter(list)
        filteradapter = filterApdater(filterlist)

        filteradapter.itemClickListener = object : filterApdater.OnItemClickListener{
            override fun OnItemClick(position: Int) {
                val selectedCategory = filteradapter.items[position]
                // 다른 RecyclerView의 데이터를 정렬하는 메서드 호출
                updateOtherRecyclerView(selectedCategory)
                filteradapter.currentIndex = position
                filteradapter.notifyDataSetChanged()
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val dividerDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerDecoration)
        adapter.itemClickListener = this
        binding.recyclerView.adapter = adapter

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.filterRecycle.setLayoutManager(layoutManager)
        binding.filterRecycle.adapter = filteradapter
    }

    // 액티비티 혹은 프래그먼트 내부에서 다른 RecyclerView를 업데이트하는 메서드
    fun updateOtherRecyclerView(category: String) {
        // 다른 RecyclerView의 어댑터를 업데이트합니다.
        // 예: filteredList를 새로운 조건에 맞게 필터링하고, 어댑터에 알립니다.
        adapter.filter("Category",category)
        adapter.notifyDataSetChanged()
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