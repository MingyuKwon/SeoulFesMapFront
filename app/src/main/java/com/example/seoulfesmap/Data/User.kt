package com.example.seoulfesmap.Data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.Serializable

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

class User {

    var uID : String? = null
    var email : String? = null
    var name : String? = null
    var profile_img_link : String? = null

}
