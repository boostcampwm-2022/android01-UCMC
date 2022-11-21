package com.gta.data.repository

import com.gta.data.source.MyPageDataSource
import com.gta.data.source.StorageDataSource
import com.gta.domain.repository.MyPageRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class MyPageRepositoryImpl @Inject constructor(
    private val myPageDataSource: MyPageDataSource,
    private val storageDataSource: StorageDataSource
) : MyPageRepository {
    override fun setThumbnail(uid: String, uri: String): Flow<String?> = callbackFlow {
        storageDataSource.uploadThumbnail(uri).addOnCompleteListener {
            if (it.isSuccessful) {
                myPageDataSource.setThumbnail(uid, it.result.toString()).addOnSuccessListener {
                    trySend(uri)
                }.addOnFailureListener {
                    trySend(null)
                }
            } else {
                trySend(null)
            }
        }
        awaitClose()
    }
}
