package com.gta.data.repository

import com.gta.domain.model.CarDetail
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class CarRepositoryImpl : CarRepository {
    override fun getOwnerId(carId: String): String {
        return "testOwnerId"
    }

    override fun getNowRentUser(carId: String): String? {
        return null
    }

    override fun getCarData(carId: String): Flow<CarDetail> {
        return MutableStateFlow(
            CarDetail(
                carId,
                "새 차 몰고 싶을 때",
                "대여 가능",
                "신당동 앞마당",
                "아반테 신형",
                183000,
                "깨끗이 써주세요. 찾아 갑니다.",
                emptyList(),
                UserProfile("(test)OwnerId", "(test)선구자", 25, null)
            )
        )
    }
}
