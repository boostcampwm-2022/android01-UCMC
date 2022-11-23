package com.gta.domain.repository

import com.gta.domain.model.LocationInfo
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    fun getSearchAddressList(query: String): Flow<List<LocationInfo>>
}
