package com.example.seoulfesmap

import android.app.Application
import android.util.Log
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.Data.FestivalHitCountService
import com.example.seoulfesmap.Data.FestivalService
import com.example.seoulfesmap.Data.PostVisitiedFestivalService
import com.example.seoulfesmap.Data.RetrofitClient
import com.example.seoulfesmap.Data.User
import com.example.seoulfesmap.Data.VisitiedFestivalService
import com.naver.maps.geometry.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class appStaticData : Application() {
    companion object {
        var USER: User? = null
        var FesDatalist: ArrayList<FestivalData> = ArrayList()
        var visitedFesDatalist: ArrayList<FestivalData> = ArrayList()


        var currentLocation : LatLng = LatLng(37.5519, 126.9918)

        fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val earthRadius = 6371.0 // 지구 반지름 (킬로미터 단위)

            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)

            val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            return earthRadius * c
        }



        fun InitFesDataList(){
            val service = RetrofitClient.getClient()!!.create(FestivalService::class.java)
            service.listFestivals()!!.enqueue(object : Callback<List<FestivalData?>?> {

                override fun onResponse(
                    call: Call<List<FestivalData?>?>,
                    response: Response<List<FestivalData?>?>
                ) {
                    if (response.isSuccessful) {
                        FesDatalist = response.body() as ArrayList<FestivalData>
                        for(fes in FesDatalist)
                        {
                            fes.changeStringToOtherType()
                        }

                        Log.d("FesStartInitalize", FesDatalist.size.toString())


                    } else {
                        // 서버 에러 처리
                        Log.e("FestivalError", "Response not successful: " + response.code())
                    }
                }

                override fun onFailure(call: Call<List<FestivalData?>?>, t: Throwable) {
                    Log.e("FestivalError", "Network error or the request was aborted", t)
                }
            })
        }

        fun initVisitedFes() {
            val service = RetrofitClient.getClient()!!.create(VisitiedFestivalService::class.java)

            if(appStaticData.USER == null) return
            service.listFestivals(appStaticData.USER!!.uID!!.toInt())!!.enqueue(object : Callback<List<FestivalData?>?> {

                override fun onResponse(
                    call: Call<List<FestivalData?>?>,
                    response: Response<List<FestivalData?>?>
                ) {
                    if (response.isSuccessful) {
                        // 성공적으로 데이터를 받아왔을 때의 처리
                        visitedFesDatalist = response.body() as ArrayList<FestivalData>
                        for (fes in visitedFesDatalist) {
                            fes.changeStringToOtherType()
                        }
                        Log.d("Profile", visitedFesDatalist.size.toString())


                    } else {
                        // 서버 에러 처리
                        Log.e("FestivalError", "Response not successful: " + response.code())
                    }
                }

                override fun onFailure(call: Call<List<FestivalData?>?>, t: Throwable) {
                    Log.e("FestivalError", "Network error or the request was aborted", t)
                }
            })
        }

        fun plusVisitedFes(fesData : FestivalData)
        {
            visitedFesDatalist.add(0,fesData)
            sendVisitedFes(fesData.fid!!)
        }


        fun sendVisitedFes(fid : Int) {
            val service = RetrofitClient.getClient()!!.create(PostVisitiedFestivalService::class.java)

            if(appStaticData.USER == null) return
            service.listFestivals(appStaticData.USER!!.uID!!.toInt(), fid)!!.enqueue(object : Callback<Void?> {

                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    if (response.isSuccessful) {
                        Log.d("Profile", visitedFesDatalist.size.toString())

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
