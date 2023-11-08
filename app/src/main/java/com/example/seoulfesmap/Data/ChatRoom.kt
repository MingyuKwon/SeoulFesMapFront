package com.example.seoulfesmap.Data

import java.io.Serializable

data class ChatRoom(
    val users: Map<String, Boolean>? = HashMap(),
    var messages: Map<String,Message>? = HashMap()
) : Serializable {
}
