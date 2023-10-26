package com.example.seoulfesmap.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import com.example.seoulfesmap.R
import com.example.seoulfesmap.databinding.FragmentHomeBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val mapView: FragmentContainerView = binding.mapFragment
        val mapFragment = childFragmentManager.findFragmentById(mapView.id) as MapFragment?
        mapFragment?.getMapAsync { mapView ->
            val cameraPosition = CameraPosition(LatLng(37.5666102, 126.9783881), 16.0)
            mapView.cameraPosition = cameraPosition

            val marker = Marker()
            marker.position = LatLng(37.5666102, 126.9783881) // 마커의 위치 설정
            marker.map = mapView

            // 다른 마커를 추가하려면 위의 코드를 반복
        }
        homeViewModel.text.observe(viewLifecycleOwner) {
            mapView.id
        }
        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map_menu_button1 -> {

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_menu, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}