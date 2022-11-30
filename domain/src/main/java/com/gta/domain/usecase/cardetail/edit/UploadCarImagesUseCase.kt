package com.gta.domain.usecase.cardetail.edit

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UploadCarImagesUseCase @Inject constructor(
    private val deleteImagesUseCase: DeleteImagesUseCase,
    private val setCarImagesUseCase: SetCarImagesUseCase
) {
    operator fun invoke(carId: String, old: List<String>, new: List<String>): Flow<List<String>> {
        val delete = old.filter { !new.contains(it) }
        val update = new.filter { !old.contains(it) }

        /*
            1. 삭제된 이미지는 저장소에서 삭제
            2. 새로 추가된 이미지만 저장소에 새로 추가
            3. 기존에 있던 이미지 + 새로 추가된 이미지 주소값 return
         */
        return deleteImagesUseCase(delete).flatMapLatest {
            setCarImagesUseCase(carId, update).map {
                old.filter { o -> new.contains(o) } + it
            }
        }
    }
}
