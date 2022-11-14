package com.gta.presentation.model

import com.gta.domain.model.CarDetail
import com.gta.domain.model.UserProfile
import timber.log.Timber

data class CarOwner(
    val id: String,
    val name: String,
    val temp: Int,
    val image: String?
) {
    fun onChattingClick() {
        Timber.d("$name : $id 랑 채팅하기")
    }

    fun onReportClick() {
        Timber.d("신고당하는 자 : $name($id)")
    }
}

fun UserProfile.toPresentation(): CarOwner = CarOwner(
    id = id,
    name = name,
    temp = temp,
    image = image
)

fun CarDetail.toCarOwner(): CarOwner = CarOwner(
    id = owner.id,
    name = owner.name,
    temp = owner.temp,
    image = owner.image
)
