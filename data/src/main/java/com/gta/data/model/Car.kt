package com.gta.data.model

import com.gta.domain.model.AvailableDate
import com.gta.domain.model.CarDetail
import com.gta.domain.model.CarRentInfo
import com.gta.domain.model.Coordinate
import com.gta.domain.model.PinkSlip
import com.gta.domain.model.RentState
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UpdateCar
import com.gta.domain.model.UserProfile

data class Car(
    val pinkSlip: PinkSlip = PinkSlip(),
    val images: List<String> = emptyList(),
    val price: Long = 10000,
    val location: String = "정보 없음",
    val coordinate: Coordinate = Coordinate(),
    val rentState: String = RentState.UNAVAILABLE.string,
    val comment: String = "정보 없음",
    val availableDate: AvailableDate = AvailableDate(),
    val ownerId: String = "정보 없음"
)

fun Car.toSimple(id: String): SimpleCar = SimpleCar(
    id = id,
    image = if (images.isNotEmpty()) images[0] else "",
    carType = pinkSlip.type,
    model = pinkSlip.model,
    year = pinkSlip.year,
    price = price.toInt(),
    coordinate = coordinate
)

fun Car.toCarRentInfo(reservationDates: List<AvailableDate>): CarRentInfo = CarRentInfo(
    images = images,
    model = pinkSlip.model,
    price = price.toInt(),
    comment = comment,
    availableDate = availableDate,
    reservationDates = reservationDates,
    ownerId = ownerId
)

fun Car.toDetailCar(id: String, owner: UserProfile): CarDetail = CarDetail(
    id = id,
    carType = pinkSlip.type,
    model = pinkSlip.model,
    year = pinkSlip.year,
    licensePlate = pinkSlip.id,
    price = price.toInt(),
    location = location,
    rentState = RentState.values().filter { it.string == rentState }[0],
    comment = comment,
    availableDate = availableDate,
    images = images,
    owner = owner,
    coordinate = coordinate
)

fun Car.update(update: UpdateCar): Car = Car(
    pinkSlip = pinkSlip,
    ownerId = ownerId,
    images = update.images,
    price = update.price.toLong(),
    comment = update.comment,
    rentState = update.rentState.string,
    availableDate = update.availableDate,
    location = update.location,
    coordinate = update.coordinate
)
