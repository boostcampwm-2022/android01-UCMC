package com.gta.data.source

import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class StorageDataSource @Inject constructor(
    private val storageReference: StorageReference
) {
    fun uploadThumbnail(uri: String): Task<Uri> {
        val image = Uri.parse(uri)
        val name = image.path?.substringAfterLast("/") ?: ""
        getExtension(image)
        val ref = storageReference
            .child("thumb")
            .child("${System.currentTimeMillis()}${name}")
        return ref.putFile(image).continueWithTask {
            ref.downloadUrl
        }
    }

    fun deleteThumbnail(path: String): Task<Void> =
        storageReference.storage.getReferenceFromUrl(path).delete()

    private fun getExtension(uri: Uri) {
        val mime = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path.toString())).toString())
        Timber.tag("mypage").i("mime $mime")
        val extension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mime)
        Timber.tag("mypage").i("ex $extension")
    }
}
