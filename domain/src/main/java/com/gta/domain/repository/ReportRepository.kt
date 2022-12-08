package com.gta.domain.repository

import com.gta.domain.model.UCMCResult

interface ReportRepository {
    suspend fun reportUser(
        uid: String,
        currentTime: Long = System.currentTimeMillis()
    ): UCMCResult<Unit>
}
