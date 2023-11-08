package com.example.seoulfesmap.ui.Chatting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seoulfesmap.Data.ChatRoom
import com.example.seoulfesmap.Data.Message
import com.example.seoulfesmap.Data.User
import com.example.seoulfesmap.R
import com.example.seoulfesmap.RecyclerView.RecyclerMessagesAdapter
import com.example.seoulfesmap.appStaticData
import com.example.seoulfesmap.databinding.ActivityChattingRoomBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone

class ChattingRoomActivity : AppCompatActivity() {

    lateinit var binding: ActivityChattingRoomBinding
    lateinit var btn_exit: ImageButton
    lateinit var btn_submit: ImageButton
    lateinit var txt_title: TextView
    lateinit var edt_message: EditText
    lateinit var firebaseDatabase: DatabaseReference
    lateinit var recycler_talks: RecyclerView
    lateinit var chatRoom: ChatRoom
    lateinit var chatRoomKey: String
    lateinit var myUid: String

    var fesName: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChattingRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProperty()
        initializeView()
        initializeListener()
        setupChatRooms()
    }

    fun initializeProperty() {  //변수 초기화
        myUid = if(appStaticData.userUid != null) appStaticData.userUid!! else ""
            //FirebaseAuth.getInstance().currentUser?.uid!!              //현재 로그인한 유저 id

        firebaseDatabase = FirebaseDatabase.getInstance().reference!!

        chatRoom = (intent.getSerializableExtra("ChatRoom")) as ChatRoom      //채팅방 정보
        chatRoomKey = intent.getStringExtra("ChatRoomKey")!!            //채팅방 키
        fesName = intent.getSerializableExtra("RoomTitle").toString()
    }

    fun initializeView() {    //뷰 초기화
        btn_exit = binding.imgbtnQuit
        edt_message = binding.edtMessage
        recycler_talks = binding.recyclerMessages
        btn_submit = binding.btnSubmit
        txt_title = binding.txtTItle
        txt_title.text = fesName.toString()
    }

    fun initializeListener() {   //버튼 클릭 시 리스너 초기화
        btn_exit.setOnClickListener()
        {
            finish()
        }
        btn_submit.setOnClickListener()
        {
            putMessage()
        }
    }

    fun setupChatRooms() {              //채팅방 목록 초기화 및 표시
        if (chatRoomKey.isNullOrBlank())
            setupChatRoomKey()
        else
            setupRecycler()
    }

    fun setupChatRoomKey() {            //chatRoomKey 없을 경우 초기화 후 목록 초기화
        FirebaseDatabase.getInstance().getReference("ChatRoom")
            .child("chatRooms").orderByChild("users/${chatRoomKey}").equalTo(true)    //상대방의 Uid가 포함된 목록이 있는지 확인
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        chatRoomKey = data.key!!          //chatRoomKey 초기화
                        setupRecycler()                  //목록 업데이트
                        break
                    }
                }
            })

    }

    fun putMessage() {       //메시지 전송
        try {
            var message = Message(myUid, getDateTimeString(), edt_message.text.toString())    //메시지 정보 초기화
            Log.i("ChatRoomKey", chatRoomKey)
            FirebaseDatabase.getInstance().getReference("ChatRoom").child("chatRooms")
                .child(chatRoomKey).child("messages")                   //현재 채팅방에 메시지 추가
                .push().setValue(message).addOnSuccessListener {
                    Log.i("putMessage", "메시지 전송에 성공하였습니다.")
                    edt_message.text.clear()
                }.addOnCanceledListener {
                    Log.i("putMessage", "메시지 전송에 실패하였습니다")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("putMessage", "메시지 전송 중 오류가 발생하였습니다.")
        }
    }

    fun getDateTimeString(): String {          //메시지 보낸 시각 정보 반환
        try {
            var localDateTime = LocalDateTime.now()
            localDateTime.atZone(TimeZone.getDefault().toZoneId())
            var dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            return localDateTime.format(dateTimeFormatter).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("getTimeError")
        }
    }

    fun setupRecycler() {            //목록 초기화 및 업데이트
        recycler_talks.layoutManager = LinearLayoutManager(this)
        recycler_talks.adapter = RecyclerMessagesAdapter(this, chatRoomKey)
    }
}