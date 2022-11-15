package com.gta.presentation.model.carDetail

import com.gta.domain.model.CarDetail
import com.gta.domain.model.UserProfile
import timber.log.Timber

data class CarOwner(
    val id: String,
    val name: String,
    val temp: String,
    val image: String?
) {
    fun onChattingClick() {
        Timber.d("$name : $id 랑 채팅하기")
    }

    fun onReportClick() {
        Timber.d("신고당하는 자 : $name($id)")
    }
}

fun UserProfile.toCarOwner(): CarOwner = CarOwner(
    id = id,
    name = name,
    temp = temp.toString(),
    image = image
)

fun CarDetail.toCarOwner(): CarOwner = CarOwner(
    id = owner.id,
    name = owner.name,
    temp = owner.temp.toString(),
    image = owner.image
)
