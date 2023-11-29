package com.example.seoulfesmap.Data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.Serializable

class User {

    var uID : String? = null
    var email : String? = null
    var name : String? = null
    var profile_img_link : String? = null

}
