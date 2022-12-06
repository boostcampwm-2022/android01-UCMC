package com.gta.data.repository

import com.gta.data.model.toLocationInfo
import com.gta.data.source.MapDataSource
import com.gta.domain.model.LocationInfo
import com.gta.domain.model.UCMCResult
import com.gta.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class MapRepositoryImpl @Inject constructor(private val mapDataSource: MapDataSource) :
    MapRepository {
    override fun getSearchAddressList(query: String): Flow<UCMCResult<List<LocationInfo>>> = flow {
        try {
            val addressResult = mapDataSource.getSearchAddressList(query)
            val keywordResult = mapDataSource.getSearchKeywordList(query)
            val result = mutableListOf<LocationInfo>()

            addressResult.documents
                .map {
                    it.toLocationInfo()
                }.also {
                    result.addAll(it)
                }

            keywordResult.documents
                .map {
                    it.toLocationInfo()
                }.also {
                    result.addAll(it)
                }
            emit(UCMCResult.Success(result))
        } catch (e: Exception) {
            emit(UCMCResult.Error(e))
        }
    }

    override fun getSearchCoordinate(longitude: String, latitude: String): Flow<String?> = flow {
        try {
            emit(
                mapDataSource.getSearchCoordinate(
                    longitude,
                    latitude
                ).documents[0].addressName.addressName
            )
        } catch (e: Exception) {
            Timber.d("실패")
        }
    }
}
