package com.example.seoulfesmap.Data

import java.time.LocalDateTime

class FestivalData(imageResource : String?, title: String?, location: String?, startTime: LocalDateTime?, endTime: LocalDateTime?) {
    var FesTitle: String? = null
    var FesLocation: String? = null
    var FesStartDate: LocalDateTime? = null
    var FesEndDate: LocalDateTime? = null

    // 여기 까지는 반드시 축제가 초기화 될 때 있어야 하는 정보들 입니다
    var imageResourceUrl : String? = null
    var homepageUrl : String? = null

    init{
        imageResourceUrl = imageResource
        FesTitle = title
        FesLocation = location
        FesStartDate = startTime
        FesEndDate = endTime
    }

}