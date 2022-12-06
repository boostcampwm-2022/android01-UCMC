package com.gta.domain.usecase.map

import com.gta.domain.model.LocationInfo
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSearchAddressUseCase @Inject constructor(private val mapRepository: MapRepository) {
    operator fun invoke(query: String): Flow<UCMCResult<List<LocationInfo>>> {
        return mapRepository.getSearchAddressList(query)
    }
}
