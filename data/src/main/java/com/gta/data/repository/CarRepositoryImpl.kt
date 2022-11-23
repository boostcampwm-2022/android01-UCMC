package com.gta.data.repository

import com.gta.data.model.Car
import com.gta.data.model.UserInfo
import com.gta.data.model.toCarRentInfo
import com.gta.data.model.toDetailCar
import com.gta.data.model.toProfile
import com.gta.data.model.toSimple
import com.gta.data.source.CarDataSource
import com.gta.data.source.ReservationDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.CarDetail
import com.gta.domain.model.CarRentInfo
import com.gta.domain.model.RentState
import com.gta.domain.model.Reservation
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CarRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val carDataSource: CarDataSource,
    private val reservationDataSource: ReservationDataSource
) : CarRepository {
    override fun getOwnerId(carId: String): Flow<String> {
        return getCar(carId).map { it.ownerId }
    }

    override fun getCarRentState(carId: String): Flow<RentState> {
        return getCar(carId).map { it.toDetailCar("", UserProfile()).rentState }
    }

    override fun getCarData(carId: String): Flow<CarDetail> = callbackFlow {
        carDataSource.getCar(carId).addOnSuccessListener { carSnapshot ->
            carSnapshot?.toObject(Car::class.java)?.let { carInfo ->
                userDataSource.getUser(carInfo.ownerId).addOnSuccessListener { ownerSnapshot ->
                    ownerSnapshot?.toObject(UserInfo::class.java)?.let { ownerInfo ->
                        trySend(
                            carInfo.toDetailCar(
                                carSnapshot.id,
                                ownerInfo.toProfile(carInfo.ownerId)
                            )
                        )
                    } ?: trySend(CarDetail())
                }.addOnFailureListener {
                    trySend(CarDetail())
                }
            } ?: trySend(CarDetail())
        }.addOnFailureListener {
            trySend(CarDetail())
        }
        awaitClose()
    }

    override fun getCarRentInfo(carId: String): Flow<CarRentInfo> = callbackFlow {
        carDataSource.getCar(carId).addOnSuccessListener { carSnapshot ->
            carSnapshot?.toObject(Car::class.java)?.let { car ->
                reservationDataSource.getAllReservations().addOnSuccessListener { collection ->
                    collection.filter { document ->
                        car.reservations.contains(document.id)
                    }.map { document ->
                        document.toObject(Reservation::class.java).reservationDate
                    }.also { reservationDates ->
                        trySend(car.toCarRentInfo(reservationDates))
                    }
                }
            }
        }.addOnFailureListener {
            trySend(CarRentInfo())
        }
        awaitClose()
    }

    private fun getCar(carId: String): Flow<Car> = callbackFlow {
        carDataSource.getCar(carId).addOnSuccessListener { snapshot ->
            snapshot?.toObject(Car::class.java)?.let {
                trySend(it)
            } ?: trySend(Car())
        }.addOnFailureListener {
            trySend(Car())
        }
        awaitClose()
    }

    override fun getSimpleCarList(ownerId: String): Flow<List<SimpleCar>> = callbackFlow {
        userDataSource.getUser(ownerId).addOnSuccessListener { user ->
            if (user.exists()) {
                val ownerCars = user.toObject(UserInfo::class.java)?.myCars
                if (ownerCars == null) {
                    trySend(listOf())
                } else {
                    carDataSource.getOwnerCars(ownerCars).addOnSuccessListener {
                        it.map { car ->
                            car.toObject(Car::class.java).toSimple(car.id)
                        }.also { result ->
                            trySend(result)
                        }
                    }.addOnFailureListener {
                        trySend(emptyList())
                    }
                }
            }
        }.addOnFailureListener {
            trySend(emptyList())
        }
        awaitClose()
    }

    override fun getAllCars(): Flow<List<SimpleCar>> = callbackFlow {
        carDataSource.getAllCars().addOnSuccessListener { query ->
            val list = mutableListOf<SimpleCar>()
            query.forEach { car ->
                list.add(car.toObject(Car::class.java).toSimple(car.id))
            }
            trySend(list)
        }.addOnFailureListener {
            trySend(emptyList())
        }
        awaitClose()
    }

    override fun removeCar(userId: String, carId: String): Flow<Boolean> = callbackFlow {
        carDataSource.removeCar(carId).addOnSuccessListener {
            userDataSource.getUser(userId).addOnSuccessListener { snapshot ->
                val myCars = snapshot.get("myCars") as List<String>
                val newCars = myCars.filter { it != carId }
                userDataSource.removeCar(userId, newCars).addOnSuccessListener {
                    trySend(true)
                }.addOnFailureListener {
                    trySend(false)
                }
            }.addOnFailureListener {
                trySend(false)
            }
        }.addOnFailureListener {
            trySend(false)
        }

        awaitClose()
    }
}
