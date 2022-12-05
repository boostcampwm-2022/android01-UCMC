package com.gta.domain.repository

interface ReportRepository {
    suspend fun reportUser(uid: String)
}
