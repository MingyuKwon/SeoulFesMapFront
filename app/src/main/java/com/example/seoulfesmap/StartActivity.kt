package com.example.seoulfesmap

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.seoulfesmap.Data.RetrofitClient
import com.example.seoulfesmap.Data.TokenGoogleService
import com.example.seoulfesmap.Data.TokenService
import com.example.seoulfesmap.Data.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

var isGuest = false
var loginNaver = false

class StartActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 123 // 원하는 숫자로 설정
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.d("result.data", result.data.toString())

        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.e("Account", account.id + " " + account.email )

                googleSignInResult(task)
                // 로그인 성공, 계정 정보 처리
            } catch (e: ApiException) {
                Log.i("GoogleLoginError" , e.toString())
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            // 로그인 창이 닫힘, 사용자가 로그인을 취소
            Log.d("cancelded", "Login canceled by user")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Log.d("Before Firebase" , "  ")
        FirebaseApp.initializeApp(this)
        Log.d("After Firebase" , "  ")



        val btnNaverLogin = findViewById<ImageView>(R.id.btn_naverLogin)
        val btnGuestLogin = findViewById<ImageView>(R.id.btn_guestLogin)
        val btnGoogleLogin = findViewById<ImageView>(R.id.btn_googleLogin)

        btnNaverLogin.setOnClickListener {
            naverLogin()
        }

        btnGuestLogin.setOnClickListener() {
            guestLogin()
        }

        btnGoogleLogin.setOnClickListener {
            googleLogin()
        }

        requestLocationPermission()
//      moveToMainActivity()
    }

    private fun guestLogin() {
        isGuest = true
        moveToMainActivity()
    }

    fun naverLogin(){
        /** Naver Login Module Initialize */
        val naverClientId = getString(R.string.social_login_info_naver_client_id)
        val naverClientSecret = getString(R.string.social_login_info_naver_client_secret)
        val naverClientName = getString(R.string.social_login_info_naver_client_name)
        NaverIdLoginSDK.initialize(this, naverClientId, naverClientSecret , naverClientName)

        var naverToken :String? = ""
        var userId :String? = ""
        var userEmail :String? = ""
        var userProfile_image :String?= ""
        var userName :String?= ""

        val profileCallback = object : NidProfileCallback<NidProfileResponse> {
            override fun onSuccess(response: NidProfileResponse) {
                userId = response.profile?.id
                userEmail = response.profile?.email
                userProfile_image = response.profile?.profileImage
                userName = response.profile?.name
                loginNaver = true
                SetUserData(userId, userEmail, userProfile_image, userName)

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

    private fun googleLogin(){
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

            val mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            val signInIntent = mGoogleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)
        } catch (e: Exception) {
            Log.w("failed", "what is wrong? : " + e);
        }
    }

    private fun googleSignInResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account = completedTask.getResult(ApiException::class.java)
            var userId = account?.id.toString()
            var userEmail = account?.email.toString()
            var userProfile_image = account?.photoUrl.toString()
            var userName = account?.displayName.toString()
            SetUserDataGoogle(userId, userEmail, userProfile_image, userName)
//            val familyname = account?.familyName
//            val givenname = account?.givenName
//            val name = familyname + givenname
        } catch (e: ApiException){
            Log.w("failed", "signInResult:failed code=" + e.statusCode)
        }
    }

    fun SetUserData(userId :String?, userEmail :String?, userProfile_image :String?, userName :String? )
    {
        Log.d("CHEKC", userId + " " + userEmail + " " + userProfile_image + " " + userName)
        val service = RetrofitClient.getClient()!!.create(TokenService::class.java)
        service.sendToken(userId, userEmail, userProfile_image, userName)!!.enqueue(object : Callback<List<User?>> {

            override fun onFailure(call: Call<List<User?>>, t: Throwable) {
                Log.e("TokenError", "Network error or the request was aborted", t)
            }

            override fun onResponse(call: Call<List<User?>>, response: Response<List<User?>>) {
                if (response.isSuccessful) {
                    // 성공적으로 데이터를 받아왔을 때의 처리
                    val jsonResponse = (response.body() as ArrayList<User>).get(0)
                    appStaticData.USER = jsonResponse
                    moveToMainActivity()
                } else {
                    // 서버 에러 처리
                    Log.e("TokenError", "Response not successful: " + response.code())
                }
            }
        })
    }

    fun SetUserDataGoogle(userId :String?, userEmail :String?, userProfile_image :String?, userName :String? )
    {
        Log.d("CHEKC", userId + " " + userEmail + " " + userProfile_image + " " + userName)
        val service = RetrofitClient.getClient()!!.create(TokenGoogleService::class.java)
        service.sendToken(userId, userEmail, userProfile_image, userName)!!.enqueue(object : Callback<List<User?>> {

            override fun onFailure(call: Call<List<User?>>, t: Throwable) {
                Log.e("TokenError", "Network error or the request was aborted", t)
            }

            override fun onResponse(call: Call<List<User?>>, response: Response<List<User?>>) {
                if (response.isSuccessful) {
                    // 성공적으로 데이터를 받아왔을 때의 처리
                    val jsonResponse = (response.body() as ArrayList<User>).get(0)
                    appStaticData.USER = jsonResponse
                    moveToMainActivity()
                } else {
                    // 서버 에러 처리
                    Log.e("TokenError", "Response not successful: " + response.code())
                }
            }
        })
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

}