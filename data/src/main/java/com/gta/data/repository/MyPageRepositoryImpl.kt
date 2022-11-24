package com.gta.data.repository

import com.gta.data.source.MyPageDataSource
import com.gta.data.source.StorageDataSource
import com.gta.domain.repository.MyPageRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MyPageRepositoryImpl @Inject constructor(
    private val myPageDataSource: MyPageDataSource,
    private val storageDataSource: StorageDataSource
) : MyPageRepository {
    override fun setThumbnail(uid: String, uri: String): Flow<String> = callbackFlow {
        val result = storageDataSource.uploadThumbnail(uri).first() ?: ""
        if (result.isNotEmpty() && myPageDataSource.setThumbnail(uid, result).first()) {
            trySend(result)
        } else {
            trySend("")
        }
        awaitClose()
    }

    override fun deleteThumbnail(path: String): Flow<Boolean> = callbackFlow {
        trySend(storageDataSource.deleteThumbnail(path).first())
        awaitClose()
    }
}
