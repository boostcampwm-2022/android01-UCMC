package com.gta.data.repository

import com.gta.data.source.MyPageDataSource
import com.gta.data.source.StorageDataSource
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.MyPageRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class MyPageRepositoryImpl @Inject constructor(
    private val myPageDataSource: MyPageDataSource,
    private val storageDataSource: StorageDataSource
) : MyPageRepository {
    override suspend fun setThumbnail(uid: String, uri: String, prevThumbnailPath: String): UCMCResult<String> {
        val result = storageDataSource.uploadPicture("users/$uid/thumbnail", uri).first() ?: ""
        return if (result.isNotEmpty() && myPageDataSource.setThumbnail(uid, result).first()) {
            storageDataSource.deletePicture(prevThumbnailPath).first()
            UCMCResult.Success(uri)
        } else {
            UCMCResult.Error(FirestoreException())
        }
    }
}
