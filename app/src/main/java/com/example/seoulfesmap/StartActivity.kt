package com.example.seoulfesmap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp

class StartActivity : AppCompatActivity() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 123 // 원하는 숫자로 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Log.d("Before Firebase" , "  ")
        FirebaseApp.initializeApp(this)
        Log.d("After Firebase" , "  ")

        val btnNaverLogin = findViewById<ImageView>(R.id.btn_naverLogin)
        val btnGoogleLogin = findViewById<ImageView>(R.id.btn_googleLogin)

        btnNaverLogin.setOnClickListener {
            naverLogin()
        }

        btnGoogleLogin.setOnClickListener {
            googleLogin()
        }

        requestLocationPermission()

    }

    fun naverLogin(){
        val naverLoginUrl = "https://konkukcapstone.dwer.kr:3000/login/naver"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(naverLoginUrl))
        startActivity(intent)
    }

    fun googleLogin(){
        val googleLoginUrl = "https://konkukcapstone.dwer.kr:3000/login/google"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(googleLoginUrl))
        startActivity(intent)
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