package com.gta.data.repository

import android.net.Uri
import com.gta.data.model.Car
import com.gta.data.model.UserInfo
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
import com.gta.domain.model.DeleteFailException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.RentState
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UCMCResult
import com.gta.domain.model.UpdateCar
import com.gta.domain.model.UpdateFailException
import com.gta.domain.model.UserNotFoundException
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    override fun getCarData(carId: String): Flow<UCMCResult<CarDetail>> = callbackFlow {
        carDataSource.getCar(carId).first()?.let { car ->
            val ownerInfo = userDataSource.getUser(car.ownerId).first() ?: UserInfo()
            trySend(
                UCMCResult.Success(
                    car.toDetailCar(
                        car.pinkSlip.informationNumber,
                        ownerInfo.toProfile(car.ownerId)
                    )
                )
            )
        } ?: trySend(UCMCResult.Error(FirestoreException()))
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

    override fun getSimpleCarList(ownerId: String): Flow<UCMCResult<List<SimpleCar>>> =
        callbackFlow {
            val userInfo = userDataSource.getUser(ownerId).first()
            if (userInfo == null) {
                trySend(UCMCResult.Error(FirestoreException()))
            } else if (userInfo.myCars.isNotEmpty()) {
                val result = carDataSource.getOwnerCars(userInfo.myCars).first()
                if (result != null) {
                    result.map { car ->
                        car.toSimple(car.pinkSlip.informationNumber)
                    }.also { mapped ->
                        trySend(UCMCResult.Success(mapped))
                    }
                } else {
                    trySend(UCMCResult.Error(FirestoreException()))
                }
            } else {
                trySend(UCMCResult.Success(emptyList()))
            }
            awaitClose()
        }

    override fun getAllCars(): Flow<List<SimpleCar>> = callbackFlow {
        val cars = carDataSource.getAllCars().first()
        trySend(cars.map { it.toSimple(it.pinkSlip.informationNumber) })
        awaitClose()
    }

    override fun getNearCars(min: Coordinate, max: Coordinate): Flow<UCMCResult<List<SimpleCar>>> =
        callbackFlow {
            val cars = carDataSource.getNearCars(min, max).first()
            if (cars == null) {
                trySend(UCMCResult.Error(FirestoreException()))
            } else {
                trySend(
                    UCMCResult.Success(
                        cars.map {
                            it.toSimple(it.pinkSlip.informationNumber)
                        }
                    )
                )
            }
            awaitClose()
        }

    override suspend fun removeCar(userId: String, carId: String): UCMCResult<Unit> {
        return if (carDataSource.removeCar(carId).first()) {
            userDataSource.getUser(userId).first()?.let { user ->
                val newCars = user.myCars.filter { it != carId }
                if (userDataSource.removeCar(userId, newCars).first()) {
                    UCMCResult.Success(Unit)
                } else {
                    UCMCResult.Error(DeleteFailException()) // TODO 삭제에 실패했을 경우 차를 다시 생성
                }
            } ?: UCMCResult.Error(UserNotFoundException()) // TODO 삭제에 실패했을 경우 차를 다시 생성
        } else {
            return UCMCResult.Error(DeleteFailException())
        }
    }

    override fun setCarImagesStorage(
        carId: String,
        images: List<String>
    ): Flow<List<UCMCResult<String>>> =
        callbackFlow {
            val imageUri = mutableListOf<UCMCResult<String>>()
            withContext(Dispatchers.IO) {
                images.forEach { img ->
                    launch {
                        val image = Uri.parse(img)
                        val name = image.path?.substringAfterLast("/") ?: ""
                        storageDataSource.uploadPicture(
                            "car/$carId/${System.currentTimeMillis()}$name",
                            img
                        ).first()?.let { uri ->
                            imageUri.add(UCMCResult.Success(uri))
                        } ?: imageUri.add(UCMCResult.Error(UpdateFailException()))
                    }
                }
            }
            trySend(imageUri)
            awaitClose()
        }

    override fun deleteImagesStorage(images: List<String>): Flow<Boolean> = callbackFlow {
        val result = mutableListOf<Boolean>()
        withContext(Dispatchers.IO) {
            images.forEach { img ->
                launch {
                    result.add(storageDataSource.deletePicture(img).first())
                }
            }
        }
        trySend(!result.contains(false))
        awaitClose()
    }
}
