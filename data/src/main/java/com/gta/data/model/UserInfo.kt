package com.gta.data.model

import com.gta.domain.model.DrivingLicense

data class UserInfo(
    val chatId: Long = 0L,
    val nickname: String = "이동훈",
    val icon: String = "none",
    val temperature: Float = 36.5f,
    val license: DrivingLicense? = null,
    val rentedCar: Long? = null,
    val myCars: List<String> = emptyList(),
    val transactionHistory: List<Long> = emptyList()
)
