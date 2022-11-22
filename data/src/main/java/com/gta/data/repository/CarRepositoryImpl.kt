package com.gta.data.repository

import com.gta.data.model.Car
import com.gta.data.model.UserInfo
import com.gta.data.model.toCarRentInfo
import com.gta.data.model.toDetailCar
import com.gta.data.model.toSimple
import com.gta.data.source.CarDataSource
import com.gta.data.source.UserDataSource
import com.gta.domain.model.CarDetail
import com.gta.domain.model.CarRentInfo
import com.gta.domain.model.RentState
import com.gta.domain.model.SimpleCar
import com.gta.domain.model.UserProfile
import com.gta.domain.repository.CarRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class CarRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
    private val carDataSource: CarDataSource
) : CarRepository {
    override fun getOwnerId(carId: String): Flow<String> {
        return getCar(carId).map { it.ownerId }
    }

    override fun getCarRentState(carId: String): Flow<RentState> {
        return getCar(carId).map { it.toDetailCar("", UserProfile()).rentState }
    }

    override fun getCarData(carId: String): Flow<CarDetail> = callbackFlow {
        // car 정보
        carDataSource.getCar(carId).addOnSuccessListener { snapshot ->
            snapshot?.toObject(Car::class.java)?.let { car ->
                // 차의 차주 id를 통해 차주 use 정보
                userDataSource.getUser(car.ownerId).addOnSuccessListener { profile ->
                    profile?.toObject(UserProfile::class.java)?.let { owner ->
                        Timber.d("car rentState ${car.rentState}")
                        trySend(car.toDetailCar(snapshot.id, owner))
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

    override fun getCarRentInfo(carId: String): Flow<CarRentInfo> {
        return getCar(carId).map {
            it.toCarRentInfo()
        }
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
