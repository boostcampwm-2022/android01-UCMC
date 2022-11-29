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
    val price: Int = 10000,
    val location: String = "동훈이 집",
    val coordinate: Coordinate = Coordinate(),
    val rentState: String = RentState.UNAVAILABLE.string,
    val comment: String = "차였어요",
    val availableDate: AvailableDate = AvailableDate(),
    val ownerId: String = ""
)

fun Car.toSimple(id: String): SimpleCar = SimpleCar(
    id = id,
    image = if (images.isNotEmpty()) images[0] else "",
    carType = pinkSlip.type,
    model = pinkSlip.model,
    year = pinkSlip.year,
    price = price,
    coordinate = coordinate
)

fun Car.toCarRentInfo(reservationDates: List<AvailableDate>): CarRentInfo = CarRentInfo(
    images = images,
    model = pinkSlip.model,
    price = price,
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
    price = price,
    location = location,
    rentState = RentState.values().filter { it.string == rentState }[0],
    comment = comment,
    availableDate = availableDate,
    images = images,
    owner = owner
)

fun Car.update(update: UpdateCar): Car = Car(
    pinkSlip = pinkSlip,
    reservations = reservations,
    ownerId = ownerId,
    images = update.images,
    price = update.price,
    comment = update.comment,
    rentState = update.rentState.string,
    availableDate = update.availableDate,
    location = update.location,
    coordinate = update.coordinate
)
