package com.example.seoulfesmap.ui.Map

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.Location
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
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.Data.FestivalHitCountService
import com.example.seoulfesmap.Data.FestivalService
import com.example.seoulfesmap.Data.RetrofitClient
import com.example.seoulfesmap.R
import com.example.seoulfesmap.databinding.FragmentHomeBinding
import com.example.seoulfesmap.ui.Popup.FesDataDialogFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val LOCATION_PERMISSION_REQUEST_CODE = 123 // 원하는 숫자로 설정

    private var list: ArrayList<FestivalData> = ArrayList()


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

        ShowMapFesDataFromServer()

        homeViewModel.text.observe(viewLifecycleOwner) {
            binding.mapFragment.id
        }
        return root
    }

    @SuppressLint("MissingPermission")
    fun initializeMap()
    {
        val locationManager: LocationManager by lazy {
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }

        val locationListener = LocationListener { location ->
            val latitude = location.latitude
            val longitude = location.longitude
            Log.d("activity", "latitude : $latitude, longitude : $longitude")
            PinMarkerInMap(latitude, longitude)
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L,
            10.0F,
            locationListener
        )
    }

    fun PinMarkerInMap(latitude : Double, longitude : Double)
    {
        val mapFragment = childFragmentManager.findFragmentById(binding.mapFragment.id) as MapFragment?
        mapFragment?.getMapAsync { mapView ->

            val currentLatLng = LatLng(latitude, longitude)
            val cameraPosition = CameraPosition(currentLatLng, 10.0)
            mapView.cameraPosition = cameraPosition

            // 현재 위치를 네이버 지도에 표시
            val currentLocationMarker = Marker()
            currentLocationMarker.icon = OverlayImage.fromResource(R.drawable.current_location_icon)
            currentLocationMarker.position = currentLatLng
            currentLocationMarker.map = mapView

            var index = 0

            val subList: List<FestivalData> = list.subList(0, 100)

            for (fesData in subList) {
                val marker = CreateFestivalMarker(fesData.xpos!!, fesData.ypos!!, index)
                marker.map = mapView
                index++
            }

        }
    }


    fun CreateFestivalMarker(x: Double, y :Double, index : Int): Marker {
        val marker = Marker()
        marker.icon = OverlayImage.fromResource(R.drawable.icon)
        marker.position = LatLng(x, y)
        marker.setOnClickListener {
            hitcountupSend(list[index].fid!!)
            showFesDataPopUp(list[index])
            marker.icon = OverlayImage.fromResource(R.drawable.current_location_icon)
            true
        }
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


    fun showFesDataPopUp(fesData : FestivalData) {
        val dialogFragment = FesDataDialogFragment(fesData)
        dialogFragment.show(requireFragmentManager(), "FesDataPopUp")
    }

    fun hitcountupSend(fid : Int)
    {


// 서비스 구현체 생성
        val service = RetrofitClient.getClient()!!.create(FestivalHitCountService::class.java)

// 요청 실행
        val call = service.incrementFestivalHit(fid)
        call!!.enqueue(object : Callback<Void?> {
            override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                if (response.isSuccessful) {
                    // 요청 성공 처리
                } else {
                    Log.e("FestivalError", "Network error or the request was aborted")
                }
            }

            override fun onFailure(call: Call<Void?>, t: Throwable) {
                Log.e("FestivalError", "Network error or the request was aborted", t)
            }
        }
        )

    }


    fun ShowMapFesDataFromServer()
    {
        val service = RetrofitClient.getClient()!!.create(FestivalService::class.java)
        service.listFestivals()!!.enqueue(object : Callback<List<FestivalData?>?>  {
            override fun onResponse(
                call: Call<List<FestivalData?>?>,
                response: Response<List<FestivalData?>?>
            ) {
                if (response.isSuccessful) {
                    // 성공적으로 데이터를 받아왔을 때의 처리
                    activity?.runOnUiThread {
                        list = response.body() as ArrayList<FestivalData>
                        for(fes in list)
                        {
                            fes.changeStringToOtherType()
                        }
                        Log.i("Before Filter", list.size.toString())
                        list = list.filter {
                            val currentDateTime = LocalDateTime.now()
                            val startDate = it.FesStartDate
                            val endDate = it.FesEndDate

                            currentDateTime.isBefore(endDate) and currentDateTime.isAfter(startDate)
                        } as ArrayList<FestivalData>
                        Log.i("After Filter", list.size.toString())


                        initializeMap()
                    }

                } else {
                    // 서버 에러 처리
                    Log.e("FestivalError", "Response not successful: " + response.code())
                }
            }

            override fun onFailure(call: Call<List<FestivalData?>?>, t: Throwable) {
                Log.e("FestivalError", "Network error or the request was aborted", t)
            }
        })

        // list.add(FestivalData(8037, "콘서트","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=43bd8ae3612e4cb2bb3a7edf9186efbf&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143909&menuNo=200008",
        //    "마포아트센터 M 레트로 시리즈 2024 신년맞이 어떤가요 #7", "마포아트센터 아트홀 맥", "2024-01-18T00:00:00.000Z" , "2024-01-18T00:00:00.000Z", "37.5499060881738", "126.945533810385"))
        // list.add(FestivalData(8038,"콘서트","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=d5e5494491b1481081180ac991c410db&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143406&menuNo=200008",
        //     "딕펑스×두번째달_Spice of life", "꿈의숲 퍼포먼스홀", "2023-12-23T00:00:00.000Z" , "2023-12-23T00:00:00.000Z", "37.6202544613023", "127.044324732036"))
        // list.add(FestivalData(8039, "전시/미술","https://culture.seoul.go.kr/cmmn/file/getImage.do?atchFileId=cc68500bcc0a4e0f89143a5a89d5facb&thumb=Y", "https://culture.seoul.go.kr/culture/culture/cultureEvent/view.do?cultcode=143763&menuNo=200009",
        //    "서울일러스트레이션페어V.16", "코엑스 B&D1홀", "2023-12-21T00:00:00.000Z", "2023-12-24T00:00:00.000Z", "37.5103947", "127.0611127")
    }



}
