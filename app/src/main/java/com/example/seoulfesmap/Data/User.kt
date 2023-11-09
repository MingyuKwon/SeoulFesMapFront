package com.example.seoulfesmap.Data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.Serializable

interface TokenService {
    @GET("login/redirect/naver")
    fun sendToken(@Query("userId") userId: String?,@Query("userEmail") userEmail: String?,@Query("userProfile_image") userProfileimage: String? ,@Query("userName") userName: String? ): Call<String?>
}
data class User(val name:String?="",
                val uid:String?="",
                val email:String?=""): Serializable {



}
