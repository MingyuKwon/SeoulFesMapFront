package com.example.seoulfesmap.Data

import java.io.Serializable

data class Message(
    var senderUid: String = "",
    var sended_date: String = "",
    var content: String = "",
    var confirmed:Boolean = false
) : Serializable {
}
