package com.gta.data.repository

import com.gta.data.source.UserDataSource
import com.gta.domain.model.CoolDownException
import com.gta.domain.model.FirestoreException
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.ReportRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : ReportRepository {

    private var lastReportedTime = 0L

    override suspend fun reportUser(
        uid: String,
        currentTime: Long
    ): UCMCResult<Unit> {
        val cooldown = REPORT_COOL_DOWN - getTimeAfterReporting(currentTime)
        return if (cooldown > 0) {
            UCMCResult.Error(CoolDownException(cooldown / 1000 + 1))
        } else {
            addReportCount(uid, currentTime)
        }
    }

    private suspend fun addReportCount(uid: String, currentTime: Long): UCMCResult<Unit> {
        return userDataSource.getUser(uid).first()?.let { user ->
            val result = userDataSource.addReportCount(uid, user.reportCount + 1).first()
            if (result) {
                lastReportedTime = currentTime
                UCMCResult.Success(Unit)
            } else {
                UCMCResult.Error(FirestoreException())
            }
        } ?: UCMCResult.Error(FirestoreException())
    }

    private fun getTimeAfterReporting(currentTime: Long): Long =
        (currentTime - lastReportedTime)

    companion object {
        private const val REPORT_COOL_DOWN = 10000
    }
}
