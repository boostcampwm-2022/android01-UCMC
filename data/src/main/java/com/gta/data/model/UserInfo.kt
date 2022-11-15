package com.gta.data.model

data class UserInfo(
    val chatId: Long = 1,
    val nickname: String = "이동훈",
    val icon: String = "none",
    val temperature: Float = 36.5f,
    val license: DrivingLicense? = null,
    val rentedCar: Long? = null,
    val myCars: List<Long> = emptyList(),
    val transactionHistory: List<Long> = emptyList()
)
