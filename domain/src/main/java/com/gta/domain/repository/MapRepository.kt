package com.gta.domain.repository

import com.gta.domain.model.LocationInfo
import com.gta.domain.model.UCMCResult
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    fun getSearchCoordinate(longitude: String, latitude: String): Flow<UCMCResult<String>>
    fun getSearchAddressList(query: String): Flow<UCMCResult<List<LocationInfo>>>
}
