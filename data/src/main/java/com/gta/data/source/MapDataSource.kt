package com.gta.data.source

import com.gta.data.model.AddressResult
import com.gta.data.model.SearchResult
import com.gta.data.service.AddressSearchService
import javax.inject.Inject

class MapDataSource @Inject constructor(private val service: AddressSearchService) {
    suspend fun getSearchAddressList(query: String): SearchResult {
        return service.requestAddressList(query)
    }

    suspend fun getSearchKeywordList(query: String): SearchResult {
        return service.requestKeywordList(query)
    }

    suspend fun getSearchCoordinate(longitude: String, latitude: String): AddressResult {
        return service.requestLocation(longitude, latitude)
    }
}
