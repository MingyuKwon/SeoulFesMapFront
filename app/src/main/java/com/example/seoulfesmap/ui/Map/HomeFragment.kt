package com.example.seoulfesmap.ui.Map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import com.example.seoulfesmap.R
import com.example.seoulfesmap.databinding.FragmentHomeBinding
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 123 // 원하는 숫자로 설정

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        requestLocationPermission()
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val locationManager: LocationManager by lazy {
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val mapView: FragmentContainerView = binding.mapFragment
        val mapFragment = childFragmentManager.findFragmentById(mapView.id) as MapFragment?
        mapFragment?.getMapAsync { mapView ->
            val cameraPosition = CameraPosition(LatLng(37.540693, 127.07023), 10.0)
            mapView.cameraPosition = cameraPosition

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                val cameraPosition = CameraPosition(currentLatLng, 10.0)
                mapView.cameraPosition = cameraPosition

                // 현재 위치를 네이버 지도에 표시
                val currentLocationMarker = Marker()
                currentLocationMarker.icon = OverlayImage.fromResource(R.drawable.current_location_icon)
                currentLocationMarker.position = currentLatLng
                currentLocationMarker.map = mapView
            }

            val marker = Marker()
            marker.icon = OverlayImage.fromResource(R.drawable.icon)
            marker.position = LatLng(37.5666102, 126.9783881) // 마커의 위치 설정
            marker.map = mapView

            val marker2 = Marker()
            marker2.icon = OverlayImage.fromResource(R.drawable.icon2)
            marker2.position = LatLng(37.540693, 127.07023) // 마커의 위치 설정
            marker2.map = mapView
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
                Log.d("HomeFragment", "Button Clicked Home Fragment");
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

    private fun requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

}