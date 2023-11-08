package com.example.seoulfesmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse

class StartActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 123 // 원하는 숫자로 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Log.d("Before Firebase" , "  ")
        FirebaseApp.initializeApp(this)
        Log.d("After Firebase" , "  ")


//        val kakaoNativeKey = getString(R.string.social_login_info_kakao_Native_key)
//        KakaoSdk.init(this, "kakao$kakaoNativeKey")
//        val btnKakaoLogin = findViewById<ImageView>(R.id.btn_kakaoLogin)
        val btnNaverLogin = findViewById<ImageView>(R.id.btn_naverLogin)

//        btnKakaoLogin.setOnClickListener {
//            // 카카오 로그인 요청
//            kakaoLogin()
//        }

        btnNaverLogin.setOnClickListener {
            naverLogin()
        }

        requestLocationPermission()

    }

    fun naverLogin(){
        /** Naver Login Module Initialize */
        val naverClientId = getString(R.string.social_login_info_naver_client_id)
        val naverClientSecret = getString(R.string.social_login_info_naver_client_secret)
        val naverClientName = getString(R.string.social_login_info_naver_client_name)
        NaverIdLoginSDK.initialize(this, naverClientId, naverClientSecret , naverClientName)

        var naverToken :String? = ""

        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(response: NidProfileResponse) {
                val userId = response.profile?.id
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(this@StartActivity, "errorCode: ${errorCode}\n" +
                        "errorDescription: ${errorDescription}", Toast.LENGTH_SHORT).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        /** OAuthLoginCallback을 authenticate() 메서드 호출 시 파라미터로 전달하거나 NidOAuthLoginButton 객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다. */
        val oauthLoginCallback = object : OAuthLoginCallback {
            override fun onSuccess() {
                // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                naverToken = NaverIdLoginSDK.getAccessToken()
//                var naverRefreshToken = NaverIdLoginSDK.getRefreshToken()
//                var naverExpiresAt = NaverIdLoginSDK.getExpiresAt().toString()
//                var naverTokenType = NaverIdLoginSDK.getTokenType()
//                var naverState = NaverIdLoginSDK.getState().toString()

                //로그인 유저 정보 가져오기
                NidOAuthLogin().callProfileApi(profileCallback)
                moveToMainActivity()
            }
            override fun onFailure(httpStatus: Int, message: String) {
                val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                Toast.makeText(this@StartActivity, "errorCode: ${errorCode}\n" +
                        "errorDescription: ${errorDescription}", Toast.LENGTH_SHORT).show()
            }
            override fun onError(errorCode: Int, message: String) {
                onFailure(errorCode, message)
            }
        }

        NaverIdLoginSDK.authenticate(this, oauthLoginCallback)
    }

    private fun moveToMainActivity()
    {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun requestLocationPermission() {
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

        if (ContextCompat.checkSelfPermission(this, fineLocationPermission) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(this, coarseLocationPermission) == PackageManager.PERMISSION_GRANTED) {
            // 권한이 이미 허용되어 있음
            // 현재 위치를 가져오는 코드를 이곳에 추가할 수 있음
        } else {
            // 권한을 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(fineLocationPermission, coarseLocationPermission),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 사용자에 의해 허용됨
                Toast.makeText(this, "위치 권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
//                moveToMainActivity()
            } else {
                // 권한이 거부됨
                Toast.makeText(this, "위치 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //    private fun kakaoLogin()
//    {
////        val kakaoAdminKey = getString(R.string.social_login_info_kakao_Admin_key)
////        val kakaoNativeKey = getString(R.string.social_login_info_kakao_Native_key)
////        val kakaoRESTKey = getString(R.string.social_login_info_kakao_REST_key)
////        KakaoSdk.init(this, "kakao$kakaoNativeKey")
//
//        // 이메일 로그인 콜백
//        val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//            if (error != null) {
//                Log.e(TAG, "로그인 실패 $error")
//            } else if (token != null) {
//                Log.e(TAG, "로그인 성공 ${token.accessToken}")
//            }
//        }
//
//        // 카카오톡 설치 확인
//        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
//            // 카카오톡 로그인
//            UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
//                // 로그인 실패 부분
//                if (error != null) {
//                    Log.e(TAG, "로그인 실패 $error")
//                    // 사용자가 취소
//                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled ) {
//                        return@loginWithKakaoTalk
//                    }
//                    // 다른 오류
//                    else {
//                        UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback) // 카카오 이메일 로그인
//                    }
//                }
//                // 로그인 성공 부분
//                else if (token != null) {
//                    Log.e(TAG, "로그인 성공 ${token.accessToken}")
//                }
//            }
//        } else {
//            UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback) // 카카오 이메일 로그인
//        }
//    }

}