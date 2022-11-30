package com.gta.data.source

import android.net.Uri
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class StorageDataSource @Inject constructor(
    private val storageReference: StorageReference
) {
    fun uploadThumbnail(uid: String, uri: String): Flow<String?> = callbackFlow {
        val image = Uri.parse(uri)
        val ref = storageReference
            .child("users")
            .child(uid)
            .child("thumbnail")
        ref.putFile(image).continueWithTask {
            ref.downloadUrl
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(it.result.toString())
            } else {
                trySend(null)
            }
        }
        awaitClose()
    }

    fun uploadPinkSlip(uid: String, uri: String): Flow<String?> = callbackFlow {
        val image = Uri.parse(uri)
        val ref = storageReference
            .child("users")
            .child(uid)
            .child("pinkslip")
        ref.putFile(image).continueWithTask {
            ref.downloadUrl
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(it.result.toString())
            } else {
                trySend(null)
            }
        }
        awaitClose()
    }

    fun uploadLicense(uid: String, uri: String): Flow<String?> = callbackFlow {
        val image = Uri.parse(uri)
        val ref = storageReference
            .child("users")
            .child(uid)
            .child("license")
        ref.putFile(image).continueWithTask {
            ref.downloadUrl
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(it.result.toString())
            } else {
                trySend(null)
            }
        }
        awaitClose()
    }

    fun saveCarImage(carId: String, uri: String): Flow<String?> = callbackFlow {
        val image = Uri.parse(uri)
        val name = image.path?.substringAfterLast("/") ?: ""
        val ref = storageReference
            .child("car")
            .child(carId)
            .child("${System.currentTimeMillis()}$name")
        ref.putFile(image).continueWithTask {
            ref.downloadUrl
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(it.result.toString())
            } else {
                trySend(null)
            }
        }
        awaitClose()
    }

    fun deleteThumbnail(path: String): Flow<Boolean> = callbackFlow {
        storageReference.storage.getReferenceFromUrl(path).delete().addOnCompleteListener {
            trySend(it.isSuccessful)
        }
        awaitClose()
    }
}
