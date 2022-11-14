package com.gta.domain.model


data class CarDetail(
    val id: String,
    val title: String,
    val state: String,
    val location: String,
    val carType: String,
    val price: Int,
    val comment: String,
    val images: List<String>,
    val owner: UserProfile
)
