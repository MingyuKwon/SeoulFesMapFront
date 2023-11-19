package com.example.seoulfesmap

import android.app.Application
import android.util.Log
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.Data.FestivalHitCountService
import com.example.seoulfesmap.Data.FestivalService
import com.example.seoulfesmap.Data.RetrofitClient
import com.example.seoulfesmap.Data.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class appStaticData : Application() {
    companion object {
        var USER: User? = null
        var FesDatalist: ArrayList<FestivalData> = ArrayList()

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
