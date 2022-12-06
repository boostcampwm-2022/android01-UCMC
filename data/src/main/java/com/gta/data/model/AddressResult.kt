package com.gta.data.model

import com.google.gson.annotations.SerializedName

data class AddressResult(
    var meta: Meta2,
    var documents: List<Document>
)

data class Meta2(
    @SerializedName("total_count") var totalCount: Int
)

data class Document(
    @SerializedName("read_address") var readAddress: RoadAddress,
    @SerializedName("address") var addressName: Address
)

data class Address(
    @SerializedName("address_name") var addressName: String,
    @SerializedName("region_1depth_name") var region1DepthName: String,
    @SerializedName("region_2depth_name") var region2DepthName: String,
    @SerializedName("region_3depth_name") var region3DepthName: String,
    @SerializedName("mountain_yn") var mountainYn: String,
    @SerializedName("main_building_no") var mainBuildingNo: String,
    @SerializedName("sub_building_no") var subBuildingNo: String

)

data class RoadAddress(
    @SerializedName("address_name") var addressName: String,
    @SerializedName("region_1depth_name") var region1DepthName: String,
    @SerializedName("region_2depth_name") var region2DepthName: String,
    @SerializedName("region_3depth_name") var region3DepthName: String,
    @SerializedName("road_name") var RoadName: String,
    @SerializedName("underground_yn") var undergroundYn: String,
    @SerializedName("main_building_no") var mainBuildingNo: String,
    @SerializedName("sub_building_no") var subBuildingNo: String,
    @SerializedName("building_name") var buildingName: String?,
    @SerializedName("zone_no") var zoneNo: String?
)
