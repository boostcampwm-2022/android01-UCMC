package com.gta.domain.repository

import com.gta.domain.model.UCMCResult

interface MyPageRepository {
    suspend fun setThumbnail(uid: String, uri: String): UCMCResult<String>
}
