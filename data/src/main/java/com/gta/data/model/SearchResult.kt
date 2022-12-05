package com.gta.data.model

import com.google.gson.annotations.SerializedName
import com.gta.domain.model.LocationInfo

data class SearchResult(
    var meta: Meta,
    var documents: List<Place>
)

data class Meta(
    @SerializedName("total_count") var totalCount: Int,
    @SerializedName("pageable_count") var pageableCount: Int,
    @SerializedName("is_end") var isEnd: Boolean
)

data class Place(
    @SerializedName("address_name") var addressName: String,
    @SerializedName("place_name") var placeName: String?,
    @SerializedName("y") var latitude: String,
    @SerializedName("x") var longitude: String
)

fun Place.toLocationInfo(): LocationInfo {
    return LocationInfo(this.addressName, this.placeName, this.latitude.toDouble(), this.longitude.toDouble())
}
