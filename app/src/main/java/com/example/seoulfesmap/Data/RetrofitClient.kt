package com.example.seoulfesmap.Data

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


class RetrofitClient {
    companion object {
        private var retrofit: Retrofit? = null
        private val BASE_URL = "https://konkukcapstone.dwer.kr:3000/"

        fun getClient(): Retrofit? {
            if (retrofit == null) {
                // Retrofit 인스턴스 생성
                val unsafeOkHttpClient = OkHttpClient.Builder().apply {
                    // Create a trust manager that does not validate certificate chains
                    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                    })

                    // Install the all-trusting trust manager
                    val sslContext = SSLContext.getInstance("SSL").apply {
                        init(null, trustAllCerts, java.security.SecureRandom())
                    }
                    sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)

                    // Don't check Hostnames, either.
                    // CAUTION: This makes the connection vulnerable to MITM attacks!
                    hostnameVerifier { _, _ -> true }
                }.build()

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(unsafeOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit
        }

    }

}