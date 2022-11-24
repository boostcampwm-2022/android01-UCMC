package com.gta.data.model

import com.gta.domain.model.LocationInfo

data class SearchResult(
    var meta: Meta,
    var documents: List<Place>
)

data class Meta(
    var total_count: Int,
    var pageable_count: Int,
    var is_end: Boolean
)

data class Place(
    var address_name: String,
    var x: String,
    var y: String
)

fun Place.toLocationInfo(): LocationInfo {
    return LocationInfo(this.address_name, this.y.toDouble(), this.x.toDouble())
}
