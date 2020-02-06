package kochanski.adam.api.model.entity

import org.springframework.data.mongodb.core.mapping.Document

@Document("district_geography")
data class DistrictGeography(
    var districtName: String,
    var coordinates: List<GeoPoint>
)
