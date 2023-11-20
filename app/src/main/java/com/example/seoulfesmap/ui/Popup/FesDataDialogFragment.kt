package com.example.seoulfesmap.ui.Popup

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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
import com.naver.maps.map.overlay.OverlayImage
import java.time.LocalDateTime

interface DialogListener {
    fun onDialogClosed()
}

class FesDataDialogFragment (var fesData : FestivalData, var dialogListener: DialogListener? = null) : DialogFragment() {

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
            stampButton.setImageResource(R.drawable.baseline_star_24)
        }else
        {
            stampButton.setImageResource(R.drawable.baseline_stardisable_24)
            val currentDate = LocalDateTime.now() // 현재 날짜와 시간
            if(
            //appStaticData.calculateDistance(fesData.xpos!!, fesData.ypos!!, appStaticData.currentLocation!!.latitude, appStaticData.currentLocation!!.longitude) < 0.05 &&
                (!currentDate.isBefore(fesData.FesStartDate) && !currentDate.isAfter(fesData.FesEndDate)))
            {
                stampButton.setImageResource(R.drawable.baseline_star_outline_24)
                stampButton.isEnabled  = true
            }
        }



        stampButton.setOnClickListener {
            plusVisitedFes(fesData)
            stampButton.setImageResource(R.drawable.baseline_star_24)
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