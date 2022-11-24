package com.gta.data.service

import com.gta.data.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query

interface AddressSearchService {
    @GET("v2/local/search/address.json")
    suspend fun requestAddressList(
        @Query("query") query: String
    ): SearchResult
}
