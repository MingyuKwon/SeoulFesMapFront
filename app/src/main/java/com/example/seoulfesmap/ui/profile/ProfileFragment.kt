package com.example.seoulfesmap.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.Data.FestivalService
import com.example.seoulfesmap.Data.RetrofitClient
import com.example.seoulfesmap.Data.VisitiedFestivalService
import com.example.seoulfesmap.RecyclerView.stickerAdapter
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.databinding.FragmentProfileBinding
import com.example.seoulfesmap.isGuest
import com.navercorp.nid.NaverIdLoginSDK
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var stickerlist: ArrayList<String> = ArrayList()
    lateinit var stickeradapter: stickerAdapter

    private var list: ArrayList<FestivalData> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val notificationsViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        initVisitedFesArray()

        if (isGuest == false) {
            binding.ProfileName.text = appStaticData.USER?.name
            binding.ProfileExplain.text = appStaticData.USER?.email

            initStickerRecyclerView()
        } else {
            binding.ProfileName.text = "Guest"
            binding.ProfileExplain.text = "게스트 모드로 접속 중입니다."
        }

        val btnLogout: ImageButton = binding.logoutBtn
        if (isGuest == false) {
            btnLogout.setOnClickListener {
                NaverLogout()
            }
        }

        val root: View = binding.root
        return root
    }

    fun initStickerRecyclerView() {
        stickerlist.add("none")
        stickerlist.add("none")
        stickerlist.add("none")
        stickerlist.add("none")
        stickerlist.add("none")


        stickeradapter = stickerAdapter(stickerlist)

        stickeradapter.itemClickListener = object : stickerAdapter.OnItemClickListener {
            override fun OnItemClick(position: Int) {
                val selectedCategory = stickeradapter.items[position]
                // 다른 RecyclerView의 데이터를 정렬하는 메서드 호출
                stickeradapter.notifyDataSetChanged()
            }
        }


        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.stickerrecyclerview.setLayoutManager(layoutManager)
        binding.stickerrecyclerview.adapter = stickeradapter
    }

    private fun NaverLogout() {
        isGuest = true
        NaverIdLoginSDK.logout()
    }

    fun initVisitedFesArray() {
        val service = RetrofitClient.getClient()!!.create(VisitiedFestivalService::class.java)

        if(appStaticData.USER == null) return
        service.listFestivals(appStaticData.USER!!.uID!!.toInt())!!.enqueue(object : Callback<List<FestivalData?>?> {

            override fun onResponse(
                call: Call<List<FestivalData?>?>,
                response: Response<List<FestivalData?>?>
            ) {
                if (response.isSuccessful) {
                    // 성공적으로 데이터를 받아왔을 때의 처리
                    activity?.runOnUiThread {
                        list = response.body() as ArrayList<FestivalData>
                        for (fes in list) {
                            fes.changeStringToOtherType()
                        }
                        Log.d("Profile", list.size.toString())
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
    }
}