package com.gta.data.model

import com.gta.domain.model.DrivingLicense
import com.gta.domain.model.UserProfile

data class UserInfo(
    val chatId: Long = 0,
    val nickname: String = "이동훈",
    val icon: String = "none",
    val temperature: Float = 36.5f,
    val license: DrivingLicense? = null,
    val rentedCar: Long? = null,
    val myCars: List<String> = emptyList(),
    val transactionHistory: List<Long> = emptyList()
)

fun UserInfo.toProfile(id: String): UserProfile = UserProfile(
    id = id,
    name = nickname,
    temp = temperature,
    image = icon
)
