package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.model.Car
import com.gta.domain.model.Coordinate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CarDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    fun getCar(carId: String): Flow<Car?> = callbackFlow {
        fireStore.collection("cars").document(carId).get().addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(it.result.toObject(Car::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose()
    }

    fun getOwnerCars(cars: List<String>): Flow<List<Car>> = callbackFlow {
        fireStore
            .collection("cars")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.filter { document ->
                        cars.contains(document.id)
                    }.map { snapshot ->
                        snapshot.toObject(Car::class.java)
                    }.also { result ->
                        trySend(result)
                    }
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose()
    }

    fun getAllCars(): Flow<List<Car>> = callbackFlow {
        fireStore.collection("cars").get().addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(it.result.map { snapshot -> snapshot.toObject(Car::class.java) })
            } else {
                trySend(emptyList())
            }
        }
        awaitClose()
    }

    fun getNearCars(min: Coordinate, max: Coordinate): Flow<List<Car>> = callbackFlow {
        fireStore.collection("cars").whereGreaterThan("coordinate.y", min.latitude)
            .whereLessThan("coordinate.y", max.latitude).get().addOnCompleteListener {
                if (it.isSuccessful) {
                    it.result.filter { document ->
                        val tmp = document.toObject(Car::class.java)
                        tmp.coordinate.longitude >= min.longitude && tmp.coordinate.longitude <= max.longitude
                    }.map { snapshot ->
                        snapshot.toObject(Car::class.java)
                    }.also { result ->
                        trySend(result)
                    }
                } else {
                    trySend(emptyList())
                }
            }
        awaitClose()
    }

    fun removeCar(carId: String): Flow<Boolean> = callbackFlow {
        fireStore.collection("cars").document(carId).delete().addOnCompleteListener {
            trySend(it.isSuccessful)
        }
        awaitClose()
    }
}
