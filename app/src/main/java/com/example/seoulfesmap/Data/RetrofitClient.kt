package com.example.seoulfesmap.Data

import android.util.Log
import com.example.seoulfesmap.appStaticData
import okhttp3.OkHttpClient
import org.checkerframework.checker.units.qual.C
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class RetrofitClient {
    companion object {
        private var retrofit: Retrofit? = null
        private val BASE_URL = "https://konkukcapstone.dwer.kr:3000/"

        fun getClient(): Retrofit? {
            if (retrofit == null) {
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

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(unsafeOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }


        suspend fun InitFesDataList(){

            val service = getClient()!!.create(FestivalService::class.java)
            try {
                val response = service.listFestivals()

                if (response!!.isSuccessful) {
                    appStaticData.FesDatalist = response.body() as ArrayList<FestivalData>
                    for(fes in appStaticData.FesDatalist)
                    {
                        fes.changeStringToOtherType()
                    }
                } else {
                    Log.e("getRecommendData Error", response.message())
                }
            } catch (e: Exception) {
                Log.e("getRecommendData Error", e.message ?: "Unknown error")
            }
        }

        suspend fun initVisitedFes() {
            if(appStaticData.USER == null) return

            Log.e("initVisitedFes", "")


            val service = getClient()!!.create(VisitiedFestivalService::class.java)
            try {
                val response = service.listFestivals(appStaticData.USER!!.uID!!.toInt())

                if (response!!.isSuccessful) {
                    appStaticData.visitedFesDatalist = response.body() as ArrayList<FestivalData>
                    for (fes in appStaticData.visitedFesDatalist) {
                        fes.changeStringToOtherType()
                    }
                } else {
                    Log.e("getRecommendData Error", response.message())
                }
            } catch (e: Exception) {
                Log.e("getRecommendData Error", e.message ?: "Unknown error")
            }
        }

        suspend fun initChallenge() {
            if(appStaticData.USER == null) return

            Log.e("initChallenge", "")


            val service = getClient()!!.create(GetChallenge::class.java)
            try {
                val response = service.getData(appStaticData.USER!!.uID!!.toInt())

                if (response!!.isSuccessful) {
                    appStaticData.challengeData = response.body()!!
                    appStaticData.challengeData.updateClearedChallenge()
                } else {
                    Log.e("getRecommendData Error", response.message())
                }
            } catch (e: Exception) {
                Log.e("getRecommendData Error", e.message ?: "Unknown error")
            }
        }

        fun plusVisitedFes(fesData : FestivalData)
        {
            appStaticData.visitedFesDatalist.add(0,fesData)
            sendVisitedFes(fesData.fid!!)
        }


        fun sendVisitedFes(fid : Int) {
            val service = RetrofitClient.getClient()!!.create(PostVisitiedFestivalService::class.java)

            if(appStaticData.USER == null) return
            service.listFestivals(appStaticData.USER!!.uID!!.toInt(), fid)!!.enqueue(object :
                Callback<Void?> {

                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    if (response.isSuccessful) {
                        Log.d("Profile", appStaticData.visitedFesDatalist.size.toString())

                    } else {
                        // 서버 에러 처리
                        Log.e("FestivalError", "Response not successful: " + response.code())
                    }
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    Log.e("FestivalError", "Network error or the request was aborted", t)
                }
            })
        }

        fun sendNewChat() {
            val service = getClient()!!.create(newChat::class.java)
            Log.e("sendNewChat1", "")

            if(appStaticData.USER == null) return
            Log.e("sendNewChat2", "")

            service.getData(appStaticData.USER!!.uID!!.toInt())!!.enqueue(object :
                Callback<Void?> {

                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    if (response.isSuccessful) {
                        Log.e("sendNewChat", "Response not successful: " + response.code())

                    } else {
                        // 서버 에러 처리
                        Log.e("sendNewChat", "Response not successful: " + response.code())
                    }
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    Log.e("sendNewChat", "Network error or the request was aborted", t)
                }
            })
        }

        fun hitcountupSend(fid : Int)
        {
            val service = RetrofitClient.getClient()!!.create(FestivalHitCountService::class.java)
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

    }

}


interface FestivalHitCountService {
    @GET("festival/hit/add")
    fun incrementFestivalHit(@Query("fID") festivalId: Int): Call<Void?>?
}

interface FestivalService {
    @GET("festival")
    suspend fun listFestivals(): Response<List<FestivalData?>?>?
}

interface FestivalHitService {
    @GET("festival/hit")
    suspend fun listFestivals(): Response<List<FestivalData?>?>?
}

interface VisitiedFestivalService {
    @GET("visit/view")
    suspend fun listFestivals(@Query("uID") userId : Int): Response<List<FestivalData?>?>?
}

interface PostVisitiedFestivalService {
    @GET("visit/add")
    fun listFestivals(@Query("uID") userId : Int, @Query("fID") fid : Int): Call<Void?>?
}

interface GetChallenge {
    @GET("challenge/view")
    suspend fun getData(@Query("uID") userId : Int): Response<Challenge?>?
}

interface newChat {
    @GET("challenge/newchat")
    fun getData(@Query("uID") userId : Int): Call<Void?>?
}

interface TokenService {
    @GET("login/redirect/naver")
    fun sendToken(@Query("userId") userId: String?,
                  @Query("userEmail") userEmail: String?,
                  @Query("userProfile_image") userProfileimage: String? ,
                  @Query("userName") userName: String? ): Call<List<User?>>
}

interface TokenGoogleService {
    @GET("login/redirect/google")
    fun sendToken(@Query("userId") userId: String?,
                  @Query("userEmail") userEmail: String?,
                  @Query("userProfile_image") userProfileimage: String? ,
                  @Query("userName") userName: String? ): Call<List<User?>>
}