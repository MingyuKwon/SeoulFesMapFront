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
import androidx.lifecycle.lifecycleScope
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
import kotlinx.coroutines.launch
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
        RetrofitClient.hitcountupSend(adapter!!.filteredList[position].fid!!);
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
        lifecycleScope.launch {
            val service = RetrofitClient.getClient()!!.create(FestivalHitService::class.java)
            try {
                val response = service.listFestivals()

                if (response!!.isSuccessful) {
                    activity?.runOnUiThread {
                        list = response.body() as ArrayList<FestivalData>
                        for(fes in list)
                        {
                            fes.changeStringToOtherType()
                        }
                        setupRecyclerView() // 여기서 RecyclerView를 초기화합니다.
                    }
                } else {
                    Log.e("getRecommendData Error", response.message())
                }
            } catch (e: Exception) {
                Log.e("getRecommendData Error", e.message ?: "Unknown error")
            }

        }

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