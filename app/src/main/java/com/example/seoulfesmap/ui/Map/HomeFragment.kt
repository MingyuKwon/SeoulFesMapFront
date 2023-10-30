package com.example.seoulfesmap.ui.Map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import com.example.seoulfesmap.R
import com.example.seoulfesmap.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

        requestLocationPermission()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val mapView: FragmentContainerView = binding.mapFragment
        val mapFragment = childFragmentManager.findFragmentById(mapView.id) as MapFragment?
        mapFragment?.getMapAsync { mapView ->
            requestLocationPermission()
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

            val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)

                    // 현재 위치를 지도에 표시
                    val marker = Marker()
                    marker.icon = OverlayImage.fromResource(R.drawable.current_location_icon)
                    marker.position = currentLatLng
                    marker.map = mapView
                }
            }
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
        val context: Context = requireContext()
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

        if (ContextCompat.checkSelfPermission(context, fineLocationPermission) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context, coarseLocationPermission) == PackageManager.PERMISSION_GRANTED) {
            // 권한이 이미 허용되어 있음
            // 현재 위치를 가져오는 코드를 이곳에 추가할 수 있음
        } else {
            // 권한을 요청
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(fineLocationPermission, coarseLocationPermission),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 사용자에 의해 허용됨
                // 현재 위치를 가져오는 코드를 이곳에 추가할 수 있음
            } else {
                // 권한이 거부됨
                Toast.makeText(requireContext(), "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


}