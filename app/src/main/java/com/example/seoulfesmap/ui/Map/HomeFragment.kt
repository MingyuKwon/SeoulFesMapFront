package com.example.seoulfesmap.ui.Map

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
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
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 123 // 원하는 숫자로 설정

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var activityContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
            val markerList = arrayOf<Marker>() // 마커들을 저장할 ArrayList
            val marker0 = createMarker(LatLng(37.5666102, 126.9783881))
            val marker1 = createMarker(LatLng(37.540693, 127.07023))
            val marker2 = createMarker(LatLng(37.567191, 127.010490))

            marker0.setOnClickListener {
                showToast("Marker 0 Clicked")

            }

            marker1.setOnClickListener {
                showToast("Marker 1 Clicked")
            }

            marker2.setOnClickListener {
                showToast("Marker 2 Clicked")
            }
            marker0.map = mapView
            marker1.map = mapView
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

    fun createMarker(position: LatLng): Marker {
        val marker = Marker()
        marker.icon = OverlayImage.fromResource(R.drawable.icon)
        marker.position = position
        return marker
    }

    private fun showToast(message: String): Boolean {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        return true
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

    private fun Marker.setOnClickListener(function: (Overlay) -> Unit) {
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_menu, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showFesDataPopUp() {
        val dialogView = LayoutInflater.from(activityContext).inflate(R.layout.fespopup, null)

        val alertDialog = AlertDialog.Builder(activityContext)
            .setView(dialogView)
            .create()

        alertDialog.show()
    }

}
