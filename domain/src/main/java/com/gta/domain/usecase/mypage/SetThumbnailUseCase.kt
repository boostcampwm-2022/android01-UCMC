package com.gta.domain.usecase.mypage

import com.gta.domain.repository.MyPageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetThumbnailUseCase @Inject constructor(
    private val repository: MyPageRepository
) {
    operator fun invoke(uid: String, uri: String): Flow<String?> =
        repository.setThumbnail(uid, uri)
}
