package com.gta.domain.usecase.cardetail.edit

import com.gta.domain.model.DeleteFailException
import com.gta.domain.model.UCMCResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class UploadCarImagesUseCase @Inject constructor(
    private val deleteImagesUseCase: DeleteImagesUseCase,
    private val setCarImagesUseCase: SetCarImagesUseCase
) {
    operator fun invoke(
        carId: String,
        oldData: List<String>,
        newData: List<String>
    ): Flow<List<UCMCResult<String>>> {
        val deleteData = oldData.filter { !newData.contains(it) }
        val updateData = newData.filter { !oldData.contains(it) }

        /*
            1. 삭제된 이미지는 저장소에서 삭제
            2. 새로 추가된 이미지만 저장소에 새로 추가
            3. 기존에 있던 이미지 + 새로 추가된 이미지 주소값 return
         */

        return deleteImagesUseCase(deleteData).combine(setCarImagesUseCase(carId, updateData)) { deleteResult, list ->
            if (!deleteResult) {
                oldData.filter { o -> newData.contains(o) }.map { UCMCResult.Success(it) } + list + UCMCResult.Error(DeleteFailException())
            } else {
                oldData.filter { o -> newData.contains(o) }.map { UCMCResult.Success(it) } + list
            }
        }
    }
}
