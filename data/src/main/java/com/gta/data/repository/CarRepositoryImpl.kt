package com.gta.data.repository

import android.net.Uri
import com.gta.data.model.Car
import com.gta.data.model.toCarRentInfo
import com.gta.data.model.toDetailCar
import com.gta.data.model.toProfile
import com.gta.data.model.toSimple
import com.gta.data.model.update
import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.StorageDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.CarDetail
import com.gta.domain.model.CarRentInfo
import com.gta.domain.model.Coordinate
import com.gta.domain.model.RentState
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UpdateCar
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CarRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val carDataSource: CarDataSource,
    private val reservationDataSource: ReservationDataSource,
    private val storageDataSource: StorageDataSource
) : CarRepository {
    override fun getOwnerId(carId: String): Flow<String> {
        return getCar(carId).map { it.ownerId }
    }

    override fun getCarRentState(carId: String): Flow<RentState> {
        return getCar(carId).map { it.toDetailCar("", UserProfile()).rentState }
    }

    override fun getCarData(carId: String): Flow<CarDetail> = callbackFlow {
        carDataSource.getCar(carId).first()?.let { car ->
            userDataSource.getUser(car.ownerId).first()?.let { ownerInfo ->
                trySend(
                    car.toDetailCar(
                        car.pinkSlip.informationNumber,
                        ownerInfo.toProfile(car.ownerId)
                    )
                )
            } ?: trySend(CarDetail())
        } ?: trySend(CarDetail())
        awaitClose()
    }

    override fun updateCarDetail(carId: String, update: UpdateCar): Flow<Boolean> = callbackFlow {
        trySend(carDataSource.createCar(carId, getCar(carId).first().update(update)).first())
        awaitClose()
    }

    override fun getCarRentInfo(carId: String): Flow<CarRentInfo> = callbackFlow {
        carDataSource.getCar(carId).first()?.let { car ->
            trySend(car.toCarRentInfo(reservationDataSource.getCarReservationDates(carId).first()))
        } ?: trySend(CarRentInfo())
        awaitClose()
    }

    override fun getSimpleCar(carId: String): Flow<SimpleCar> = callbackFlow {
        carDataSource.getCar(carId).first()?.let { car ->
            trySend(car.toSimple(carId))
        } ?: trySend(SimpleCar())
        awaitClose()
    }

    private fun getCar(carId: String): Flow<Car> = callbackFlow {
        val car = carDataSource.getCar(carId).first() ?: Car()
        trySend(car)
        awaitClose()
    }

    override fun getSimpleCarList(ownerId: String): Flow<List<SimpleCar>> = callbackFlow {
        userDataSource.getUser(ownerId).first()?.let { userInfo ->
            if (userInfo.myCars.isNotEmpty()) {
                carDataSource.getOwnerCars(userInfo.myCars).first().map { car ->
                    car.toSimple(car.pinkSlip.informationNumber)
                }.also { result ->
                    trySend(result)
                }
            } else {
                trySend(emptyList())
            }
        } ?: trySend(emptyList())
        awaitClose()
    }

    override fun getAllCars(): Flow<List<SimpleCar>> = callbackFlow {
        val cars = carDataSource.getAllCars().first()
        trySend(cars.map { it.toSimple(it.pinkSlip.informationNumber) })
        awaitClose()
    }

    override fun getNearCars(min: Coordinate, max: Coordinate): Flow<List<SimpleCar>> =
        callbackFlow {
            val cars = carDataSource.getNearCars(min, max).first()
            trySend(cars.map { it.toSimple(it.pinkSlip.informationNumber) })
            awaitClose()
        }

    override fun removeCar(userId: String, carId: String): Flow<Boolean> = callbackFlow {
        if (carDataSource.removeCar(carId).first()) {
            userDataSource.getUser(userId).first()?.let { userInfo ->
                val newCars = userInfo.myCars.filter { it != carId }
                trySend(userDataSource.removeCar(userId, newCars).first())
            } ?: trySend(false)
        } else {
            trySend(false)
        }
        awaitClose()
    }

    override fun setCarImagesStorage(carId: String, images: List<String>): Flow<List<String>> =
        callbackFlow {
            val imageUri = mutableListOf<String>()
            images.forEach { img ->
                val image = Uri.parse(img)
                val name = image.path?.substringAfterLast("/") ?: ""
                storageDataSource.uploadPicture("car/$carId/${System.currentTimeMillis()}$name", img).first()?.let { uri ->
                    imageUri.add(uri)
                }
            }
            trySend(imageUri)
            awaitClose()
        }

    override fun deleteImagesStorage(images: List<String>): Flow<Boolean> = callbackFlow {
        val result = mutableListOf<Boolean>()
        images.forEach { img ->
            result.add(storageDataSource.deletePicture(img).first())
        }
        trySend(!result.contains(false))
        awaitClose()
    }
}
