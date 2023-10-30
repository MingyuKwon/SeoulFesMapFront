package com.example.seoulfesmap.ui.Hot

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
import com.example.seoulfesmap.databinding.FragmentNotificationsBinding
import java.time.LocalDateTime

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private var list: ArrayList<FestivalData> = ArrayList()
    lateinit var adapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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


        return root
    }

    fun initRecyclerView()
    {
        list.add(FestivalData(R.drawable.fesimageexample,  "아무튼 신나는 축제", "건국대 앞", LocalDateTime.of(2023, 10, 20, 9, 0) , LocalDateTime.of(2023, 11, 10, 21, 0)))
        list.add(FestivalData(R.drawable.fesimageexample2,  "아무튼 신나는 축제 2", "건국대 뒤", LocalDateTime.of(2023, 12, 25, 9, 0) , LocalDateTime.of(2023, 12, 31, 21, 0)))


        adapter = RecyclerAdapter(list)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        val dividerDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)

        binding.recyclerView.addItemDecoration(dividerDecoration)


        binding.recyclerView.adapter = adapter

    }

    ///////////////////////////////// option Button /////////////////////////////////

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.hot_menu_button1 -> {
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