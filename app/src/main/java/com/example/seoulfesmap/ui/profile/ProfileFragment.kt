package com.example.seoulfesmap.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seoulfesmap.RecyclerView.stickerAdapter
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.databinding.FragmentProfileBinding
import com.example.seoulfesmap.isGuest
import com.navercorp.nid.NaverIdLoginSDK

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var stickerlist: ArrayList<String> = ArrayList()
    lateinit var stickeradapter: stickerAdapter


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

        if(isGuest == false) {
            binding.ProfileName.text = appStaticData.USER?.name
            binding.ProfileExplain.text = appStaticData.USER?.email

            initStickerRecyclerView()
        }
        else {
            binding.ProfileName.text = "Guest"
            binding.ProfileExplain.text = "게스트 모드로 접속 중입니다."
        }

        val btnLogout: ImageButton = binding.logoutBtn
        if(isGuest == false) {
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

        stickeradapter.itemClickListener = object : stickerAdapter.OnItemClickListener{
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

    private fun NaverLogout(){
        isGuest = true
        NaverIdLoginSDK.logout()
    }
}