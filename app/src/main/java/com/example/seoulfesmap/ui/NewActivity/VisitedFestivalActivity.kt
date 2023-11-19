package com.example.seoulfesmap.ui.NewActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.R
import com.example.seoulfesmap.RecyclerView.RecyclerAdapter
import com.example.seoulfesmap.RecyclerView.filterApdater
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.databinding.ActivityMainBinding
import com.example.seoulfesmap.databinding.ActivityVisitedFestivalBinding
import com.example.seoulfesmap.databinding.FragmentDashboardBinding
import java.util.ArrayList

class VisitedFestivalActivity : AppCompatActivity() {
    lateinit var adapter: RecyclerAdapter

    private var _binding: ActivityVisitedFestivalBinding? = null
    private val binding get() = _binding!!



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visited_festival)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true) // 뒤로 가기 버튼 활성화

        _binding = ActivityVisitedFestivalBinding.inflate(layoutInflater)
        setContentView(binding.root) // 수정된 부분

        initRecyclerView()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish() // 현재 액티비티 종료
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun initRecyclerView()
    {
        adapter = RecyclerAdapter(appStaticData.visitedFesDatalist, true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val dividerDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerDecoration)
        binding.recyclerView.adapter = adapter

        for(a in appStaticData.visitedFesDatalist)
        {
            Log.d("Visited", a.toString())
        }
    }


}