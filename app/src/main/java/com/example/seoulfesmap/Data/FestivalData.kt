package com.example.seoulfesmap.Data

import java.time.LocalDateTime

class FestivalData(id: Int, title: String?, location: String?, startTime: LocalDateTime?, endTime: LocalDateTime?) {

    private var imageResourceID = 0
    private var FesTitle: String? = null
    private var FesLocation: String? = null
    private var FesStartDate: LocalDateTime? = null
    private var FesEndDate: LocalDateTime? = null

    init{
        imageResourceID = id
        FesTitle = title
        FesLocation = location
        FesStartDate = startTime
        FesEndDate = endTime
    }



    fun getImageResourceID(): Int {
        return imageResourceID
    }

    fun getFesTitle(): String? {
        return FesTitle
    }

    fun getFesLocation(): String? {
        return FesLocation
    }

    fun getFesStartDate(): LocalDateTime? {
        return FesStartDate
    }

    fun getFesEndDate(): LocalDateTime? {
        return FesEndDate
    }



    fun setImageResourceID(imageResourceID: Int) {
        this.imageResourceID = imageResourceID
    }

    fun setFesTitle(FesTitle : String?){
        this.FesTitle = FesTitle
    }

    fun setFesLocation(FesLocation : String?) {
        this.FesLocation = FesLocation
    }

    fun setFesStartDate(FesStartDate : LocalDateTime?){
        this.FesStartDate = FesStartDate
    }

    fun setFesEndDate(FesStartDate : LocalDateTime?) {
        this.FesStartDate = FesStartDate
    }
}