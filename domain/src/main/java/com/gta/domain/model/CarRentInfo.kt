package com.gta.domain.model

/*
    carDetail 클래스를 재사용하여 같이 사용하고자 하였으나, 차주 정보도 받아 오는 구조여서
    일단 새로 만듬
    추후 논의
 */
data class CarRentInfo(
    val images: List<String>,
    val model: String,
    val price: Int = 10000,
    val comment: String = "차였어요",
    val availableDate: AvailableDate = AvailableDate(),
    val reservations: List<String> = emptyList()
)