package com.gta.domain.model

data class UserProfile(
    val id: String = "정보 없음",
    val name: String = "정보 없음",
    val temp: Float = 0.0f,
    val image: String? = null
)