package com.gta.domain.usecase.mypage

import com.gta.domain.repository.MyPageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteThumbnailUseCase @Inject constructor(
    private val repository: MyPageRepository
) {
    operator fun invoke(uid: String, path: String): Flow<Boolean> =
        repository.deleteThumbnail(uid, path)
}
