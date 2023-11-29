package com.example.seoulfesmap.Data

import android.util.Log
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime



class FestivalData(FID : Int?, Category : String?,imageResource : String?, homepageurl : String?,title: String?, location: String?, startTime: String?, endTime: String?, Xpos: String?, Ypos: String?) {

    @SerializedName("fID")
    var fid : Int? = null

    @SerializedName("code_name")
    var category : String? = null

    @SerializedName("main_img")
    var imageResourceUrl : String? = null

    @SerializedName("hmpg_addr")
    var homepageUrl : String? = null

    @SerializedName("title")
    var FesTitle: String? = null

    @SerializedName("place")
    var FesLocation: String? = null
    @SerializedName("start_date")
    var start_date: String? = null
    @SerializedName("end_date")
    var end_date: String? = null

    var FesStartDate: LocalDateTime? = null
    var FesEndDate: LocalDateTime? = null

    @SerializedName("xpos")
    var xpos: Double? = null

    @SerializedName("ypos")
    var ypos: Double? = null

    @SerializedName("hit")
    var hit: Int? = null

    @SerializedName("time")
    var visitedDate: String? = null

    @SerializedName("gu_name")
    var gu_name: String? = null


    override fun toString(): String {
        var str = ""
        str += fid
        str += "\n"

        str += category
        str += "\n"

        str += imageResourceUrl
        str += "\n"

        str += homepageUrl
        str += "\n"

        str += FesTitle
        str += "\n"

        str += FesLocation
        str += "\n"

        str += start_date
        str += "\n"

        str += end_date
        str += "\n"

        str += xpos
        str += "\n"

        str += ypos
        str += "\n"

        Log.i("FesData" , str)
        return super.toString()
    }
    init{
        fid = FID
        category = Category
        imageResourceUrl = imageResource
        homepageUrl = homepageurl
        FesTitle = title
        FesLocation = location

        start_date = startTime
        end_date = endTime

        xpos = Xpos!!.toDouble()
        ypos = Ypos!!.toDouble()

        changeStringToOtherType()
    }

    fun changeStringToOtherType()
    {
        FesStartDate = ZonedDateTime.parse(start_date)
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()
        FesEndDate = ZonedDateTime.parse(end_date)
            .withZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()

    }

}