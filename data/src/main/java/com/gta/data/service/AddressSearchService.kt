package com.gta.data.service

import com.gta.data.model.AddressResult
import com.gta.data.model.SearchResult
import retrofit2.http.GET
import retrofit2.http.Query

interface AddressSearchService {
    @GET("v2/local/search/address.json")
    suspend fun requestAddressList(
        @Query("query") query: String
    ): SearchResult

    @GET("v2/local/search/keyword.json")
    suspend fun requestKeywordList(
        @Query("query") query: String
    ): SearchResult

    @GET("v2/local/geo/coord2address.json")
    suspend fun requestLocation(
        @Query("x") longitude: String,
        @Query("y") latitude: String
    ): AddressResult
}
