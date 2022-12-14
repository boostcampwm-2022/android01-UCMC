package com.gta.domain.usecase.user

import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.ReportRepository
import javax.inject.Inject

class ReportUserUseCase @Inject constructor(
    private val repository: ReportRepository
) {
    suspend operator fun invoke(uid: String): UCMCResult<Unit> =
        repository.reportUser(uid)
}
