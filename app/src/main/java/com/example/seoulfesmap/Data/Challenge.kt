package com.example.seoulfesmap.Data

import android.util.Log
import com.example.seoulfesmap.R
import com.example.seoulfesmap.appStaticData.Companion.stampCount
import com.example.seoulfesmap.appStaticData.Companion.stickerCount

class Challenge {
    var uID : String? = null
    var stamp1 : String? = null
    var stamp10 : String? = null
    var stamp100 : String? = null
    var stamp1000 : String? = null
    var sticker1 : String? = null
    var sticker5 : String? = null
    var sticker10 : String? = null
    var newchat : String? = null
    var gods : String? = null
    var login : String? = null

    var clearedList : ArrayList<String> = ArrayList()
    var unclearedList : ArrayList<String> = ArrayList()
    var totlaList : ArrayList<String> = ArrayList()

    val functionMap: Map<String, () -> Int> = mapOf(
        "stamp1" to {
            100 * stampCount / 1
                    },
        "stamp10" to {
            100 * stampCount / 10
        },
        "stamp100" to {
            100 * stampCount / 100
        },
        "stamp1000" to {
            100 * stampCount / 1000
        },

        "sticker1" to {
            100 * stickerCount / 1
        },

        "sticker5" to {
            100 * stickerCount / 5
        },

        "sticker10" to {
            100 * stickerCount / 10
        },

        "newchat" to {
            if(newchat == "1")
            {
                100
            }else
            {
                0
            }
        },

        "gods" to {
            if(gods == "1")
            {
                100
            }else
            {
                0
            }
        },

        "login" to {
            if(login == "1")
            {
                100
            }else
            {
                0
            }
        }
    )


    val imageMap: Map<String, Int> = mapOf(
        "stamp1" to R.drawable.stamp_01,
        "stamp10" to R.drawable.stamp_10,
        "stamp100" to R.drawable.stamp_100,
        "stamp1000" to R.drawable.stamp_1000,
        "sticker1" to R.drawable.sticker1,
        "sticker5" to R.drawable.sticker5,
        "sticker10" to R.drawable.sticker10,
        "newchat" to R.drawable.chatting,
        "gods" to R.drawable.gods,
        "login" to R.drawable.login,
    )

    val unableimageMap: Map<String, Int> = mapOf(
        "stamp1" to R.drawable.stamp_01unable,
        "stamp10" to R.drawable.stamp_10unable,
        "stamp100" to R.drawable.stamp_100unable,
        "stamp1000" to R.drawable.stamp_1000unable,
        "sticker1" to R.drawable.sticker1_unable,
        "sticker5" to R.drawable.sticker5_unable,
        "sticker10" to R.drawable.sticker10_unable,
        "newchat" to R.drawable.chatting_unable,
        "gods" to R.drawable.gods_unable,
        "login" to R.drawable.login_unable,
    )

    val titleMap: Map<String, String> = mapOf(
        "stamp1" to "서울 축제 입문!",
        "stamp10" to "서울 축제 초보!",
        "stamp100" to "서울 축제 좀 치네!",
        "stamp1000" to "서울 축제 마스터",
        "sticker1" to "스티커 수집 시작!",
        "sticker5" to "스티커 수집 이렇게 하는건가?",
        "sticker10" to "이제 스티커 수집 좀 하는군!",
        "newchat" to "축제 오픈런!",
        "gods" to "G.O.D.S.",
        "login" to "서울 축제 즐길 준비 완료!",
    )

    val descriptionMap: Map<String, String> = mapOf(
        "stamp1" to "첫 축제 스탬프를 찍었습니다",
        "stamp10" to "축제 스탬프 10개를 찍었습니다.",
        "stamp100" to "축제 스탬프 100개를 찍었습니다.",
        "stamp1000" to "축제 스탬프 1000개를 찍었습니다.",
        "sticker1" to "첫 스티커를 획득했습니다",
        "sticker5" to "스티커 5개를 수집했습니다.",
        "sticker10" to "스티커 10개를 수집했습니다.",
        "newchat" to "커뮤니티 단체 채팅방을 최초로 개설했습니다",
        "gods" to "ㄱ,ㅇ,ㄷ,ㅅ으로 시작되는 축제에 모두 참가했습니다.",
        "login" to "로그인 성공",
    )

    fun updateClearedChallenge()
    {
        clearedList.clear()
        unclearedList.clear()
        totlaList.clear()

        if(stamp1 != "0") clearedList.add("stamp1")
        else unclearedList.add("stamp1")

        if(stamp10 != "0") clearedList.add("stamp10")
        else unclearedList.add("stamp10")

        if(stamp100 != "0") clearedList.add("stamp100")
        else unclearedList.add("stamp100")

        if(stamp1000 != "0") clearedList.add("stamp1000")
        else unclearedList.add("stamp1000")

        if(sticker1 != "0") clearedList.add("sticker1")
        else unclearedList.add("sticker1")

        if(sticker5 != "0") clearedList.add("sticker5")
        else unclearedList.add("sticker5")

        if(sticker10 != "0") clearedList.add("sticker10")
        else unclearedList.add("sticker10")

        if(newchat != "0") clearedList.add("newchat")
        else unclearedList.add("newchat")

        if(gods != "0") clearedList.add("gods")
        else unclearedList.add("gods")

        if(login != "0") clearedList.add("login")
        else unclearedList.add("login")

        totlaList = (clearedList + unclearedList) as ArrayList<String>
    }

    override fun toString(): String {
        var str = ""
        str += uID
        str += "\n"

        str += stamp1
        str += "\n"

        str += stamp10
        str += "\n"

        str += stamp100
        str += "\n"

        str += stamp1000
        str += "\n"

        str += sticker1
        str += "\n"

        str += sticker5
        str += "\n"

        str += sticker10
        str += "\n"

        str += newchat
        str += "\n"

        str += gods
        str += "\n"

        str += login
        str += "\n"

        Log.i("FesData" , str)
        return super.toString()
    }
}