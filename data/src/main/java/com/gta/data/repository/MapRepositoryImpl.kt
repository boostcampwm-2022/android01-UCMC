package com.gta.data.repository

import com.gta.data.model.toLocationInfo
import com.gta.data.source.MapDataSource
import com.gta.domain.model.LocationInfo
import com.gta.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(private val mapDataSource: MapDataSource) :
    MapRepository {
    override fun getSearchAddressList(query: String): Flow<List<LocationInfo>> = flow {
        try {
            val result = mapDataSource.getSearchAddressList(query)
            result.documents
                .map {
                    it.toLocationInfo()
                }.also {
                    emit(it)
                }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
