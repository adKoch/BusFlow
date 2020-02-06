package kochanski.adam.busflowpicker.model.entities

import org.springframework.data.mongodb.core.mapping.Document

@Document("district_geography")
data class DistrictGeography(
    var districtName: String,
    var coordinates: List<GeoPoint>
)
