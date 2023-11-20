package com.example.seoulfesmap.ui.List

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.R
import com.example.seoulfesmap.RecyclerView.RecyclerAdapter
import com.example.seoulfesmap.RecyclerView.filterApdater
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.databinding.FragmentDashboardBinding
import com.example.seoulfesmap.ui.Popup.CalendarDialogFragment
import com.example.seoulfesmap.ui.Popup.CalendarDialogListener
import com.example.seoulfesmap.ui.Popup.FesDataDialogFragment
import java.time.LocalDate
import java.time.format.DateTimeParseException


class DashboardFragment : Fragment(), RecyclerAdapter.OnItemClickListener, CalendarDialogListener {

    private var selectedDate: LocalDate? = null
    private var _binding: FragmentDashboardBinding? = null
    private lateinit var activityContext: Context

    private var filterlist: ArrayList<String> = ArrayList()
    lateinit var adapter: RecyclerAdapter
    lateinit var filteradapter: filterApdater

    var dateStartFilter : String = ""
    var dateEndFilter : String = ""
    var categoryFilter : String = "전체"


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activityContext = requireActivity()
    }

    override fun onDateSelected(startDate: String?, endDate: String?) {
        dateStartFilter = startDate!!
        dateEndFilter = endDate!!

        try {
            val start = startDate?.let { LocalDate.parse(it) }
        } catch (e: DateTimeParseException) {
            dateStartFilter = ""
        }

        try {
            val end = endDate?.let { LocalDate.parse(it) }
        } catch (e: DateTimeParseException) {
            dateEndFilter = ""
        }

        adapter.filter(categoryFilter,dateStartFilter, dateEndFilter)
    }
    fun showCalendarDialog() {
        val dialogFragment = CalendarDialogFragment(dateStartFilter, dateEndFilter)
        dialogFragment.calendarDialogListener = this
        dialogFragment.show(requireFragmentManager(), "calendarDialog")
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

        setupRecyclerView()

        return root
    }

    fun showFesDataPopUp(fesData : FestivalData) {
        val dialogFragment = FesDataDialogFragment(fesData)
        dialogFragment.show(requireFragmentManager(), "FesDataPopUp")
    }

    override fun OnItemClick(position: Int) {
        appStaticData.hitcountupSend(adapter.filteredList[position].fid!!)
        showFesDataPopUp(adapter.filteredList[position])
    }

    private fun setupRecyclerView() {

        val uniqueCategories = appStaticData.FesDatalist.map { it.category }.toSet().filterNotNull()
        val uniqueCategoriesList = ArrayList(uniqueCategories)
        filterlist.add("전체")
        filterlist.addAll(uniqueCategoriesList)

        adapter = RecyclerAdapter(appStaticData.FesDatalist)
        filteradapter = filterApdater(filterlist) {
            position ->

        }

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

    fun initRecyclerView()
    {
        // list.add(FestivalData(8037, "콘서트","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=43bd8ae3612e4cb2bb3a7edf9186efbf&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143909&menuNo=200008",
        //    "마포아트센터 M 레트로 시리즈 2024 신년맞이 어떤가요 #7", "마포아트센터 아트홀 맥", "2024-01-18T00:00:00.000Z" , "2024-01-18T00:00:00.000Z", "37.5499060881738", "126.945533810385"))
       // list.add(FestivalData(8038,"콘서트","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=d5e5494491b1481081180ac991c410db&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143406&menuNo=200008",
       //     "딕펑스×두번째달_Spice of life", "꿈의숲 퍼포먼스홀", "2023-12-23T00:00:00.000Z" , "2023-12-23T00:00:00.000Z", "37.6202544613023", "127.044324732036"))
       // list.add(FestivalData(8039, "전시/미술","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=cc68500bcc0a4e0f89143a5a89d5facb&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143763&menuNo=200009",
        //    "서울일러스트레이션페어V.16", "코엑스 B&D1홀", "2023-12-21T00:00:00.000Z", "2023-12-24T00:00:00.000Z", "37.5103947", "127.0611127")
    }

    // 액티비티 혹은 프래그먼트 내부에서 다른 RecyclerView를 업데이트하는 메서드
    fun updateOtherRecyclerView(category: String) {
        // 다른 RecyclerView의 어댑터를 업데이트합니다.
        // 예: filteredList를 새로운 조건에 맞게 필터링하고, 어댑터에 알립니다.
        categoryFilter = category
        adapter.filter(categoryFilter,dateStartFilter, dateEndFilter)
        adapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.list_menu_button1-> {
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