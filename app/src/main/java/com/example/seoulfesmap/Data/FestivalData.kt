package com.example.seoulfesmap.Data

import java.time.LocalDateTime

class FestivalData(FID : Int?, Category : String?,imageResource : String?, homepageurl : String?,title: String?, location: String?, startTime: LocalDateTime?, endTime: LocalDateTime?, Xpos: String?, Ypos: String?) {
    var fid : Int? = null
    var category : String? = null
    var imageResourceUrl : String? = null
    var homepageUrl : String? = null
    var FesTitle: String? = null
    var FesLocation: String? = null
    var FesStartDate: LocalDateTime? = null
    var FesEndDate: LocalDateTime? = null

    var xpos: Double? = null
    var ypos: Double? = null


    init{
        fid = FID
        category = Category
        imageResourceUrl = imageResource
        homepageUrl = homepageurl
        FesTitle = title
        FesLocation = location
        FesStartDate = startTime
        FesEndDate = endTime

        xpos = Xpos!!.toDouble()
        ypos = Ypos!!.toDouble()
    }

}