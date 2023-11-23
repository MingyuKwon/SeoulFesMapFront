package com.example.seoulfesmap.ui.Hot

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.seoulfesmap.Data.FestivalHitService
import com.example.seoulfesmap.Data.RetrofitClient
import com.example.seoulfesmap.R
import com.example.seoulfesmap.RecyclerView.RecyclerAdapter
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.databinding.FragmentNotificationsBinding
import com.example.seoulfesmap.ui.Popup.FesDataDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NotificationsFragment : Fragment(), RecyclerAdapter.OnItemClickListener {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityContext: Context

    private var list: ArrayList<FestivalData> = ArrayList()
    var adapter: RecyclerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activityContext = requireActivity();
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecyclerView()
        initCurrentTimeTextView()

        return root
    }

    fun initCurrentTimeTextView()
    {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM월 dd일 HH:mm")
        val formattedDateTime = currentDateTime.format(formatter)
        binding.textView.text = formattedDateTime + " 현재 인기 TOP 10"
    }

    fun showFesDataPopUp(fesData : FestivalData) {
        val dialogFragment = FesDataDialogFragment(fesData)
        dialogFragment.show(requireFragmentManager(), "FesDataPopUp")
    }


    override fun OnItemClick(position: Int) {
        appStaticData.hitcountupSend(adapter!!.filteredList[position].fid!!);
        showFesDataPopUp(adapter!!.filteredList[position])
    }
    private fun setupRecyclerView() {
        if(adapter == null)
        {
            adapter = RecyclerAdapter(list)
            binding.recyclerView.layoutManager = LinearLayoutManager(context)
            val dividerDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
            binding.recyclerView.addItemDecoration(dividerDecoration)
            adapter!!.itemClickListener = this
            binding.recyclerView.adapter = adapter
        }else
        {
            adapter!!.refillData(list)
        }

    }

    fun initRecyclerView()
    {
        val service = RetrofitClient.getClient()!!.create(FestivalHitService::class.java)
        service.listFestivals()!!.enqueue(object : Callback<List<FestivalData?>?> {

            override fun onResponse(
                call: Call<List<FestivalData?>?>,
                response: Response<List<FestivalData?>?>
            ) {
                if (response.isSuccessful) {
                    // 성공적으로 데이터를 받아왔을 때의 처리
                    activity?.runOnUiThread {
                        list = response.body() as ArrayList<FestivalData>
                        for(fes in list)
                        {
                            fes.changeStringToOtherType()
                        }
                        setupRecyclerView() // 여기서 RecyclerView를 초기화합니다.
                    }

                } else {
                    // 서버 에러 처리
                    Log.e("FestivalError", "Response not successful: " + response.code())
                }
            }

            override fun onFailure(call: Call<List<FestivalData?>?>, t: Throwable) {
                Log.e("FestivalError", "Network error or the request was aborted", t)
            }
        })

        // list.add(FestivalData(8037, "콘서트","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=43bd8ae3612e4cb2bb3a7edf9186efbf&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143909&menuNo=200008",
        //    "마포아트센터 M 레트로 시리즈 2024 신년맞이 어떤가요 #7", "마포아트센터 아트홀 맥", "2024-01-18T00:00:00.000Z" , "2024-01-18T00:00:00.000Z", "37.5499060881738", "126.945533810385"))
        // list.add(FestivalData(8038,"콘서트","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=d5e5494491b1481081180ac991c410db&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143406&menuNo=200008",
        //     "딕펑스×두번째달_Spice of life", "꿈의숲 퍼포먼스홀", "2023-12-23T00:00:00.000Z" , "2023-12-23T00:00:00.000Z", "37.6202544613023", "127.044324732036"))
        // list.add(FestivalData(8039, "전시/미술","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=cc68500bcc0a4e0f89143a5a89d5facb&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143763&menuNo=200009",
        //    "서울일러스트레이션페어V.16", "코엑스 B&D1홀", "2023-12-21T00:00:00.000Z", "2023-12-24T00:00:00.000Z", "37.5103947", "127.0611127")
    }

    ///////////////////////////////// option Button /////////////////////////////////

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.hot_menu_button1 -> {
                initRecyclerView()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.hot_menu, menu)
    }

    ///////////////////////////////// option Button /////////////////////////////////

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}