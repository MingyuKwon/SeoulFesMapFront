package com.example.seoulfesmap

import android.app.Application
import com.example.seoulfesmap.Data.User

class appStaticData : Application() {
    companion object {
        var USER: User? = null
    }
}
