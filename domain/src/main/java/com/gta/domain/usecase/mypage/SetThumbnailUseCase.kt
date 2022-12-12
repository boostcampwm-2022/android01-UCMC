package com.gta.domain.usecase.mypage

import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.MyPageRepository
import javax.inject.Inject

class SetThumbnailUseCase @Inject constructor(
    private val repository: MyPageRepository
) {
    suspend operator fun invoke(uid: String, uri: String): UCMCResult<String> =
        repository.setThumbnail(uid, uri)
}
