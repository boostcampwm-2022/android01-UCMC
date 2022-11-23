package com.gta.domain.model

data class ReviewDTO(
    val opponent: UserProfile = UserProfile(),
    val carImage: String = "",
    val carModel: String = ""
)
