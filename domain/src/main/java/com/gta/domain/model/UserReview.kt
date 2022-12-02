package com.gta.domain.model

data class UserReview(
    val from: String = "정보 없음",
    val comment: String = "",
    val rating: Float = 5.0f
)
