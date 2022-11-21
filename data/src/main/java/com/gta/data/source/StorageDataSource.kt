package com.gta.data.source

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import javax.inject.Inject

class StorageDataSource @Inject constructor(
    private val storageReference: StorageReference
) {
    fun uploadThumbnail(uri: String): Task<Uri> {
        val image = Uri.parse(uri)
        val name = image.path?.substringAfterLast("/") ?: ""
        val ref = storageReference
            .child("thumb")
            .child("${name}${System.currentTimeMillis()}.jpg")
        return ref.putFile(image).continueWithTask {
            ref.downloadUrl
        }
    }

    fun deleteThumbnail(path: String): Task<Void> =
        storageReference.storage.getReferenceFromUrl(path).delete()
}
