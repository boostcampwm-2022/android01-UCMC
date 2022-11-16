package com.gta.presentation.util

import android.content.ContentResolver
import androidx.core.net.toUri
import java.nio.ByteBuffer

class ImageUtil(
    private val contentResolver: ContentResolver
) {
    fun getByteBuffer(uri: String): ByteBuffer? {
        var byteBuffer: ByteBuffer? = null
        runCatching {
            contentResolver.openInputStream(uri.toUri())
        }.onSuccess { inputStream ->
            inputStream?.use {
                byteBuffer = ByteBuffer.wrap(it.readBytes())
            }
        }
        return byteBuffer
    }
}
