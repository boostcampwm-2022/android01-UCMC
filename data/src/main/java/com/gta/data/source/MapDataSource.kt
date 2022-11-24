package com.gta.data.source

import com.gta.data.model.SearchResult
import com.gta.data.service.AddressSearchService
import javax.inject.Inject

class MapDataSource @Inject constructor(private val service: AddressSearchService) {
    suspend fun getSearchAddressList(query: String): SearchResult {
        return service.requestAddressList(query = query)
    }
}
