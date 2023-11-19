package com.example.seoulfesmap.ui.Map

import kotlin.math.*

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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.Data.FestivalHitCountService
import com.example.seoulfesmap.Data.FestivalService
import com.example.seoulfesmap.Data.RetrofitClient
import com.example.seoulfesmap.R
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.appStaticData.Companion.hitcountupSend
import com.example.seoulfesmap.databinding.FragmentHomeBinding
import com.example.seoulfesmap.ui.Popup.FesDataDialogFragment
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import kotlinx.coroutines.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 123 // 원하는 숫자로 설정

    private var showFeslist: ArrayList<FestivalData> = ArrayList()
    private var visitedlist: ArrayList<FestivalData> = ArrayList()

    private lateinit  var currentLocation : LatLng

    private val markers = mutableListOf<Marker>()




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
            Log.d("activity", "latitude : $latitude, longitude : $longitude")
            currentLocation = LatLng(latitude, longitude)
            PinMarkerInMap(latitude, longitude,10.0)
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

            Log.d("NaverMap", "Center: $currentCenter, Zoom: $currentZoom")
        }

    }

    fun getVisitedFes()
    {
        visitedlist.add(FestivalData(8833, "교육/체험", "https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=4205c385c5304e0d8285b9214ef8a231&thumb=Y"
        ,"https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143127&menuNo=200011",
            "국립극장 공연예술박물관 어린이 해설 및 견학 프로그램 [별별공연탐험대]", "국립극장 공연예술박물관, 국립극장 해오름극장",
            "2023-09-05T00:00:00.000Z", "2023-12-01T00:00:00.000Z", "0", "0"))
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
            currentLocationMarker.position = currentLocation
            currentLocationMarker.map = mapView
            markers.add(currentLocationMarker)

            // 현재 위치를 네이버 지도에 표시

            var index = 0

            val subList = showFeslist.filter {
                val distance = calculateDistance(it.xpos!!, it.ypos!!, latitude, longitude)
                distance < 5
            } as ArrayList<FestivalData>

            for (fesData in subList) {
                val marker = CreateFestivalMarker(fesData.xpos!!, fesData.ypos!!, index)
                marker.map = mapView
                markers.add(marker)
                index++
            }

        }
    }

    fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371.0 // 지구 반지름 (킬로미터 단위)

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }


    fun CreateFestivalMarker(x: Double, y :Double, index : Int): Marker {
        val marker = Marker()
        if(visitedlist.any { it.fid == showFeslist[index].fid!!})
        {
            marker.icon = OverlayImage.fromResource(R.drawable.baseline_star_24)
        }else
        {
            marker.icon = OverlayImage.fromResource(R.drawable.icon)
        }
        marker.position = LatLng(x, y)
        marker.setOnClickListener {
            Log.i("Markertouch", showFeslist[index].toString())
            appStaticData.hitcountupSend(showFeslist[index].fid!!)
            marker.icon = OverlayImage.fromResource(R.drawable.icon3)
            showFesDataPopUp(showFeslist[index])
            true
        }
        return marker
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


    fun showFesDataPopUp(fesData : FestivalData) {
        val dialogFragment = FesDataDialogFragment(fesData)
        dialogFragment.show(requireFragmentManager(), "FesDataPopUp")
    }



    fun ShowMapFesDataFromServer()
    {
        showFeslist = appStaticData.FesDatalist.toMutableList() as ArrayList<FestivalData>
        for(fes in showFeslist)
        {
            fes.changeStringToOtherType()
        }
        showFeslist = showFeslist.filter {
            val currentDateTime = LocalDateTime.now()
            val startDate = it.FesStartDate
            val endDate = it.FesEndDate

            currentDateTime.isBefore(endDate) and currentDateTime.isAfter(startDate)
        } as ArrayList<FestivalData>

        // list.add(FestivalData(8037, "콘서트","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=43bd8ae3612e4cb2bb3a7edf9186efbf&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143909&menuNo=200008",
        //    "마포아트센터 M 레트로 시리즈 2024 신년맞이 어떤가요 #7", "마포아트센터 아트홀 맥", "2024-01-18T00:00:00.000Z" , "2024-01-18T00:00:00.000Z", "37.5499060881738", "126.945533810385"))
        // list.add(FestivalData(8038,"콘서트","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=d5e5494491b1481081180ac991c410db&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143406&menuNo=200008",
        //     "딕펑스×두번째달_Spice of life", "꿈의숲 퍼포먼스홀", "2023-12-23T00:00:00.000Z" , "2023-12-23T00:00:00.000Z", "37.6202544613023", "127.044324732036"))
        // list.add(FestivalData(8039, "전시/미술","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=cc68500bcc0a4e0f89143a5a89d5facb&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143763&menuNo=200009",
        //    "서울일러스트레이션페어V.16", "코엑스 B&D1홀", "2023-12-21T00:00:00.000Z", "2023-12-24T00:00:00.000Z", "37.5103947", "127.0611127")
    }



}
