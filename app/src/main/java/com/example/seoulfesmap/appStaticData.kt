package com.example.seoulfesmap

import android.app.Application
import android.util.Log
import com.example.seoulfesmap.Data.Challenge
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
        var challengeData: Challenge = Challenge()
        var stickerItems = arrayOf("newbee","none","none","none","none","none","none","none","none",)

        fun resetStickerItem()
        {
            stickerItems = arrayOf("newbee","none","none","none","none","none","none","none","none",)
        }

        val stampCount get() = visitedFesDatalist.size
        val stickerCount get() : Int
        {
            var count = 0
            for(a in stickerItems)
            {
                if(a != "none")
                {
                    count++
                }
            }

            return count
        }




        var currentLocation : LatLng = LatLng(37.5519, 126.9918)

        fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
            val earthRadius = 6371.0 // 지구 반지름 (킬로미터 단위)

            val dLat = Math.toRadians(lat2 - lat1)
            val dLon = Math.toRadians(lon2 - lon1)

            val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
            val c = 2 * atan2(sqrt(a), sqrt(1 - a))

            return earthRadius * c
        }

    }
}
