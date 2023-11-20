package com.example.seoulfesmap.ui.Map

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationListener
import android.location.LocationManager
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
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.R
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.databinding.FragmentHomeBinding
import com.example.seoulfesmap.isGuest
import com.example.seoulfesmap.ui.Popup.DialogListener
import com.example.seoulfesmap.ui.Popup.FesDataDialogFragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import kotlinx.coroutines.*
import java.time.LocalDateTime
import kotlin.math.*

class HomeFragment : Fragment(), DialogListener {

    private var _binding: FragmentHomeBinding? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 123 // 원하는 숫자로 설정

    private var showFeslist: ArrayList<FestivalData> = ArrayList()

    private val markers = mutableListOf<Marker>()

    private var isStickerMode = false



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var activityContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activityContext = requireActivity()

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

        initializeMapInCurrentLocation()

        binding.renewButton.setOnClickListener(){
            initializeMapLocation()
        }
        binding.gotoCurrentLoationButton.setOnClickListener(){
            initializeMapInCurrentLocation()
        }

        homeViewModel.text.observe(viewLifecycleOwner) {
            binding.mapFragment.id
        }
        return root
    }

    @SuppressLint("MissingPermission")
    fun initializeMapInCurrentLocation()
    {

        val locationManager: LocationManager by lazy {
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        val locationListener = LocationListener { location ->
            val latitude = location.latitude
            val longitude = location.longitude
            appStaticData.currentLocation = LatLng(latitude, longitude)
            PinMarkerInMap(latitude, longitude,12.0)
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L,
            10.0F,
            locationListener
        )
    }

    fun initializeMapLocation()
    {
        val mapFragment = childFragmentManager.findFragmentById(binding.mapFragment.id) as MapFragment?
        mapFragment?.getMapAsync { naverMap ->
            // 다른 작업 수행...

            val currentCameraPosition = naverMap.cameraPosition
            val currentCenter = currentCameraPosition.target // 중심점 LatLng
            val currentZoom = currentCameraPosition.zoom // 줌 레벨

            PinMarkerInMap(currentCameraPosition.target.latitude, currentCameraPosition.target.longitude,  currentZoom)
        }

    }

    fun ClearMarker()
    {
        for (marker in markers) {
            marker.map = null // 마커 제거
        }
        markers.clear()
    }

    fun PinMarkerInMap(latitude : Double, longitude : Double, zoom : Double)
    {
        if(_binding == null) return
        ShowMapFesDataFromServer()
        val mapFragment = childFragmentManager.findFragmentById(binding.mapFragment.id) as MapFragment?
        mapFragment?.getMapAsync { mapView ->

            mapView.cameraPosition = CameraPosition(LatLng(latitude, longitude), zoom)

            ClearMarker()

            val currentLocationMarker = Marker()
            currentLocationMarker.icon = OverlayImage.fromResource(R.drawable.baseline_location_on_24)
            currentLocationMarker.position = appStaticData.currentLocation!!
            currentLocationMarker.map = mapView
            markers.add(currentLocationMarker)

            // 현재 위치를 네이버 지도에 표시

            if(isStickerMode)
            {
                for (fesData in showFeslist) {
                    val isVisited = appStaticData.visitedFesDatalist.any { it.fid == fesData.fid!!}

                    if(isVisited)
                    {
                        val marker = CreateFestivalMarker(fesData)
                        marker.map = mapView
                        markers.add(marker)
                    }
                }
            }else
            {
                var subList = showFeslist.filter {
                    val distance = appStaticData.calculateDistance(it.xpos!!, it.ypos!!, latitude, longitude)
                    distance < 4
                } as ArrayList<FestivalData>

                for (fesData in subList) {
                    val isVisited = appStaticData.visitedFesDatalist.any { it.fid == fesData.fid!!}
                    if(!isVisited)
                    {
                        val marker = CreateFestivalMarker(fesData)
                        marker.map = mapView
                        markers.add(marker)
                    }
                }
            }
        }
    }

    fun CreateFestivalMarker(fesData: FestivalData): Marker {
        val marker = Marker()
        if(isStickerMode) {
            marker.icon = OverlayImage.fromResource(R.drawable.baseline_star_24)
        } else {
            marker.icon = OverlayImage.fromResource(R.drawable.icon)
        }

        marker.position = LatLng(fesData.xpos!!, fesData.ypos!!)
        marker.setOnClickListener {
            Log.i("Markertouch", fesData.toString())
            appStaticData.hitcountupSend(fesData.fid!!)
            showFesDataPopUp(fesData)
            true
        }
        return marker
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map_menu_button1 -> {
                if(isGuest) return true

                isStickerMode = !isStickerMode
                initializeMapLocation()
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


    fun showFesDataPopUp(fesData : FestivalData) {
        val dialogFragment = FesDataDialogFragment(fesData, this)
        dialogFragment.show(requireFragmentManager(), "FesDataPopUp")
    }

    fun ShowMapFesDataFromServer()
    {
        showFeslist = appStaticData.FesDatalist.toMutableList() as ArrayList<FestivalData>
        showFeslist = showFeslist.filter {
            val currentDateTime = LocalDateTime.now()
            val startDate = it.FesStartDate
            val endDate = it.FesEndDate

            !currentDateTime.isBefore(startDate) && !currentDateTime.isAfter(endDate)
        } as ArrayList<FestivalData>

        Log.d("ShowMapFesDataFromServer", showFeslist.size.toString())

    }

    override fun onDialogClosed() {
        initializeMapLocation()
    }


}