package com.gta.data.source

import com.google.firebase.firestore.FirebaseFirestore
import com.gta.data.model.Car
import com.gta.domain.model.Coordinate
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CarDataSource @Inject constructor(
    private val fireStore: FirebaseFirestore
) {
    // 실시간
    fun getCar(carId: String): Flow<Car?> = callbackFlow {
        val job =
            fireStore.collection("cars").document(carId).addSnapshotListener { result, error ->
                if (error != null) {
                    close()
                    return@addSnapshotListener
                }
                trySend(result?.toObject(Car::class.java))
            }

        awaitClose {
            job.remove()
        }
    }

    suspend fun getSuspendCar(carId: String): Car? {
        return fireStore.collection("cars")
            .document(carId)
            .get()
            .await()
            .toObject(Car::class.java)
    }

    fun getOwnerCars(cars: List<String>): Flow<List<Car>?> = callbackFlow {
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
                    trySend(null)
                }
            }
        awaitClose()
    }

    /*
        PinkSlipDataSource의 createCar와 동일한 코드
     */
    fun createCar(carId: String, car: Car): Flow<Boolean> =
        callbackFlow {
            fireStore
                .collection("cars")
                .document(carId)
                .set(car)
                .addOnCompleteListener {
                    trySend(it.isSuccessful)
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

    fun getNearCars(min: Coordinate, max: Coordinate): Flow<List<Car>?> = callbackFlow {
        fireStore.collection("cars").whereGreaterThan("coordinate.latitude", min.latitude)
            .whereLessThan("coordinate.latitude", max.latitude).get().addOnCompleteListener {
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
                    trySend(null)
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
