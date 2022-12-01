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
    fun uploadPicture(path: String, uri: String): Flow<String?> = callbackFlow {
        val image = Uri.parse(uri)
        val ref = storageReference.child(path)
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

    fun deletePicture(path: String): Flow<Boolean> = callbackFlow {
        storageReference.storage.getReferenceFromUrl(path).delete().addOnCompleteListener {
            trySend(it.isSuccessful)
        }
        awaitClose()
    }
}
