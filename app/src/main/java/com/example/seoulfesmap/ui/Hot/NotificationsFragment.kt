package com.example.seoulfesmap.ui.Hot

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.seoulfesmap.Data.ChatRoom
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.Data.FestivalHitCountService
import com.example.seoulfesmap.Data.FestivalHitService
import com.example.seoulfesmap.Data.FestivalService
import com.example.seoulfesmap.Data.User
import com.example.seoulfesmap.R
import com.example.seoulfesmap.RecyclerView.RecyclerAdapter
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.databinding.FragmentNotificationsBinding
import com.example.seoulfesmap.ui.Chatting.ChattingRoomActivity
import com.google.android.play.integrity.internal.t
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class NotificationsFragment : Fragment(), RecyclerAdapter.OnItemClickListener {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityContext: Context

    private var list: ArrayList<FestivalData> = ArrayList()
    lateinit var adapter: RecyclerAdapter

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
        val dialogView = LayoutInflater.from(activityContext).inflate(R.layout.fespopup, null)

        val alertDialog = AlertDialog.Builder(activityContext)
            .setView(dialogView)
            .create()

        val fesImage : ImageView = dialogView.findViewById(R.id.imageView)

        val titleText : TextView = dialogView.findViewById(R.id.FesTitle)
        val locationText : TextView = dialogView.findViewById(R.id.FestLocation)
        val dateText : TextView = dialogView.findViewById(R.id.FesDate)

        val detailbutton: Button = dialogView.findViewById(R.id.button1)
        val communitybutton: Button = dialogView.findViewById(R.id.button2)
        val closebutton: Button = dialogView.findViewById(R.id.button3)


        Glide.with(activityContext)
            .load(fesData.imageResourceUrl)
            .into(fesImage)

        titleText.text = fesData.FesTitle
        locationText.text = fesData.FesLocation
        dateText.text = fesData.start_date!!.toString()

        detailbutton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(fesData.homepageUrl)
            }
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(requireActivity(), "링크를 열 수 있는 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        communitybutton.setOnClickListener {
            moveToChattingRoom(fesData.fid!!, fesData.FesTitle!!)
        }
        closebutton.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()

        val window = alertDialog.window
        if (window != null) {
            val size = Point()
            val display = window.windowManager.defaultDisplay
            display.getSize(size)
            // 화면 너비의 일정 비율로 팝업 크기를 설정할 수 있습니다.
            window.setLayout((size.x * 1).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun moveToChattingRoom(fesId : Int, fesName: String)
    {
        val exampleChatRoom = ChatRoom(users= mapOf(appStaticData.USER?.uID!! to true))
// 채팅방 키를 미리 알고 있다고 가정하거나 서버로부터 얻어와야 함
        val chatRoomKey = fesId.toString()

// Intent 생성
        val intent = Intent(context, ChattingRoomActivity::class.java).apply {
            putExtra("ChatRoom", exampleChatRoom)
            putExtra("ChatRoomKey", chatRoomKey)
            putExtra("RoomTitle", fesName)
        }

// Activity 시작
        startActivity(intent)
    }

    override fun OnItemClick(position: Int) {
        hitcountupSend(adapter.filteredList[position].fid!!);
        showFesDataPopUp(adapter.filteredList[position])
    }
    fun hitcountupSend(fid : Int)
    {
        // Retrofit 인스턴스 생성
        val unsafeOkHttpClient = OkHttpClient.Builder().apply {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL").apply {
                init(null, trustAllCerts, java.security.SecureRandom())
            }
            sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)

            // Don't check Hostnames, either.
            // CAUTION: This makes the connection vulnerable to MITM attacks!
            hostnameVerifier { _, _ -> true }
        }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://konkukcapstone.dwer.kr:3000/")
            .client(unsafeOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

// 서비스 구현체 생성
        val service = retrofit.create(FestivalHitCountService::class.java)

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

    private fun setupRecyclerView() {
        adapter = RecyclerAdapter(list)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val dividerDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerDecoration)
        adapter.itemClickListener = this
        binding.recyclerView.adapter = adapter
    }

    fun initRecyclerView()
    {
        val unsafeOkHttpClient = OkHttpClient.Builder().apply {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL").apply {
                init(null, trustAllCerts, java.security.SecureRandom())
            }
            sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)

            // Don't check Hostnames, either.
            // CAUTION: This makes the connection vulnerable to MITM attacks!
            hostnameVerifier { _, _ -> true }
        }.build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://konkukcapstone.dwer.kr:3000/")
            .client(unsafeOkHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val service = retrofit.create(FestivalHitService::class.java)
        service.listFestivals()!!.enqueue(object : Callback<List<FestivalData?>?> {

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
                        setupRecyclerView() // 여기서 RecyclerView를 초기화합니다.
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