package com.gta.domain.model

data class Car(
    val pinkSlip: PinkSlip = PinkSlip(),
    val images: List<String> = emptyList(),
    val price: Int = 10000,
    val location: String = "동훈이 집",
    val coordinate: Coordinate = Coordinate(),
    val rentState: String = CarState.UNAVAILABLE.string,
    val comment: String = "차였어요",
    val availableDate: AvailableDate = AvailableDate(),
    val reservations: List<String> = emptyList(),
    val ownerId: String = ""
)

fun Car.toSimple(id: String): SimpleCar = SimpleCar(
    id = id,
    image = if (images.size > 1) images[0] else "",
    carType = pinkSlip.type,
    model = pinkSlip.model,
    price = price,
    coordinate = coordinate
)
