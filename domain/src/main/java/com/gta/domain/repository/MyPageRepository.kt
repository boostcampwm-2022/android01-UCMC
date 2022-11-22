package com.gta.domain.repository

import kotlinx.coroutines.flow.Flow

interface MyPageRepository {
    fun setThumbnail(uid: String, uri: String): Flow<String?>

    fun deleteThumbnail(path: String): Flow<Boolean>
}
