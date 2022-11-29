package com.gta.data.model

import com.google.gson.annotations.SerializedName

data class MessageResult(
    val success: Int,
    val failure: Int,
    val results: List<MessageIdList>
)

data class MessageIdList(@SerializedName("message_id") val messageId: String)
