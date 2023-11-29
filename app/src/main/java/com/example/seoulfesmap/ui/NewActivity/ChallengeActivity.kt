package com.example.seoulfesmap.ui.NewActivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seoulfesmap.R
import com.example.seoulfesmap.RecyclerView.ChallengeAdapter
import com.example.seoulfesmap.RecyclerView.RecyclerAdapter
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.databinding.ActivityChallengeBinding
import com.example.seoulfesmap.databinding.ActivityVisitedFestivalBinding

class ChallengeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChallengeBinding
    lateinit var adapter: ChallengeAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChallengeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()
    }

    fun initRecyclerView()
    {
        adapter = ChallengeAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val dividerDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerDecoration)
        binding.recyclerView.adapter = adapter
    }

}