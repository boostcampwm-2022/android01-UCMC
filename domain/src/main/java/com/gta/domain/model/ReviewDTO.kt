package com.gta.domain.model

data class ReviewDTO(
    val reviewType: ReviewType = ReviewType.LENDER_TO_OWNER,
    val opponent: UserProfile = UserProfile(),
    val carImage: String = "",
    val carModel: String = ""
)
