package com.example.seoulfesmap.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.Data.FestivalService
import com.example.seoulfesmap.Data.RetrofitClient
import com.example.seoulfesmap.R
import com.example.seoulfesmap.RecyclerView.RecyclerAdapter
import com.example.seoulfesmap.RecyclerView.filterApdater
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var stickerlist: ArrayList<String> = ArrayList()
    lateinit var stickeradapter: filterApdater


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

        binding.ProfileName.text = appStaticData.USER?.name
        binding.ProfileExplain.text = appStaticData.USER?.email

        initStickerRecyclerView()

        val root: View = binding.root
        return root
    }

    fun initStickerRecyclerView() {
        stickerlist.add("전체")
        stickeradapter = filterApdater(stickerlist)

        stickeradapter.itemClickListener = object : filterApdater.OnItemClickListener{
            override fun OnItemClick(position: Int) {
                val selectedCategory = stickeradapter.items[position]
                // 다른 RecyclerView의 데이터를 정렬하는 메서드 호출
                stickeradapter.currentIndex = position
                stickeradapter.notifyDataSetChanged()
            }
        }


        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.stickerrecyclerview.setLayoutManager(layoutManager)
        binding.stickerrecyclerview.adapter = stickeradapter
    }
}