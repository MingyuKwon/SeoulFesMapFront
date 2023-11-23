package com.example.seoulfesmap.ui.Popup

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.seoulfesmap.Data.ChatRoom
import com.example.seoulfesmap.Data.FestivalData
import com.example.seoulfesmap.R
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.appStaticData.Companion.plusVisitedFes
import com.example.seoulfesmap.ui.NewActivity.ChattingRoomActivity
import java.time.LocalDateTime

interface DialogListener {
    fun onDialogClosed()
}

class FesDataDialogFragment (var fesData : FestivalData, var dialogListener: DialogListener? = null) : DialogFragment() {
    var seoulDistrict:Array<String> = arrayOf("강서구", "양천구", "구로구", "영등포구", "금천구",
        "동작구", "관악구", "서초구", "강남구", "송파구", "강동구", "은평구", "서대문구", "마포구", "종로구",
        "중구", "용산구", "강북구", "성북구", "도봉구", "노원구", "중랑구", "동대문구", "성동구", "광진구")

    val images = arrayOf(R.drawable.gangseo, R.drawable.yangchun, R.drawable.guro, R.drawable.yeongdeongpo, R.drawable.geumchun,
        R.drawable.dongjak, R.drawable.kwanak, R.drawable.seocho, R.drawable.gangnam, R.drawable.songpa, R.drawable.gangdong,
        R.drawable.eunpyeong, R.drawable.seodaemun, R.drawable.mapo, R.drawable.jongro, R.drawable.jung, R.drawable.yongsan,
        R.drawable.gangbuk, R.drawable.seocho, R.drawable.dobong, R.drawable.nowon, R.drawable.jungrang, R.drawable.dongdaemun,
        R.drawable.seodaemun, R.drawable.kwangjin)

    val tournames = arrayOf("소악루", "조계종 국제선센터", "고척스카이돔", "밤도깨비 야시장", "예술의 시간",
        "수산물 도매시장", "151동 미술관", "예술의 전당", "별마당 도서관", "롯데월드", "광나루 한강공원",
        "역사 한옥 박물관", "자연사 박물관", "하늘공원", "쌈지길", "DDP", "N서울타워", "북서울 꿈의 숲",
        "정릉", "창포원", "서울시립 과학관", "중랑장미공원", "서울풍물시장", "응봉산 팔각정", "일감호")
    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog{
        val dialogView = LayoutInflater.from(context).inflate(R.layout.fespopup, null)

        val fesImage : ImageView = dialogView.findViewById(R.id.imageView)

        val titleText : TextView = dialogView.findViewById(R.id.FesTitle)
        val locationText : TextView = dialogView.findViewById(R.id.FestLocation)
        val dateStartText : TextView = dialogView.findViewById(R.id.FesDateStart)
        val dateEndText : TextView = dialogView.findViewById(R.id.FesDateEnd)


        val detailbutton: Button = dialogView.findViewById(R.id.button1)
        val communitybutton: Button = dialogView.findViewById(R.id.button2)
        val closebutton: Button = dialogView.findViewById(R.id.button3)

        val stampButton : ImageButton = dialogView.findViewById(R.id.Stamp)
        stampButton.isEnabled  = false

        Glide.with(requireContext())
            .load(fesData.imageResourceUrl)
            .into(fesImage)

        titleText.text = fesData.FesTitle
        locationText.text = fesData.FesLocation
        dateStartText.text = fesData.FesStartDate!!.toString()
        dateEndText.text = fesData.FesEndDate!!.toString()

        val districtName: TextView = dialogView.findViewById(R.id.district)
        val tourName : TextView = dialogView.findViewById(R.id.tour)
        val tourImage : ImageView = dialogView.findViewById(R.id.tourView)
        for(i: Int in 0 .. 24) {
            if(fesData.gu_name == seoulDistrict[i]) {
                districtName.text = seoulDistrict[i]
                tourName.text = tournames[i]
                tourImage.setImageResource(images[i])
            }
        }


        detailbutton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(fesData.homepageUrl)
            }
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(requireActivity(), "링크를 열 수 있는 앱이 설치되어 있지 않습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        communitybutton.setOnClickListener {
            moveToChattingRoom(fesData.fid!!, fesData.FesTitle!!)
        }
        closebutton.setOnClickListener {
            dismiss()
        }

        if(appStaticData.visitedFesDatalist.any { it.fid == fesData.fid!!})
        {
            stampButton.setImageResource(R.drawable.stamp_ok)
        }else
        {
            stampButton.setImageResource(R.drawable.stamp_no)
            val currentDate = LocalDateTime.now() // 현재 날짜와 시간
            if(
            //appStaticData.calculateDistance(fesData.xpos!!, fesData.ypos!!, appStaticData.currentLocation!!.latitude, appStaticData.currentLocation!!.longitude) < 0.05 &&
                (!currentDate.isBefore(fesData.FesStartDate) && !currentDate.isAfter(fesData.FesEndDate)))
            {
                stampButton.setImageResource(R.drawable.stamp_clickable)
                stampButton.isEnabled  = true
            }
        }



        stampButton.setOnClickListener {
            plusVisitedFes(fesData)
            stampButton.setImageResource(R.drawable.stamp_ok)
            stampButton.isEnabled  = false
        }

        return AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
    }


    override fun onDetach() {
        super.onDetach()
        dialogListener?.onDialogClosed()
    }


    private fun moveToChattingRoom(fesId : Int, fesName: String)
    {
        val exampleChatRoom = ChatRoom(users= mapOf(appStaticData.USER?.uID!! to true))
// 채팅방 키를 미리 알고 있다고 가정하거나 서버로부터 얻어와야 함
        val chatRoomKey = fesId.toString()

// Intent 생성
        val intent = Intent(context, ChattingRoomActivity::class.java).apply {
            putExtra("ChatRoom", exampleChatRoom)
            putExtra("ChatRoomKey", chatRoomKey)
            putExtra("RoomTitle", fesName)
        }

// Activity 시작
        startActivity(intent)
    }


}