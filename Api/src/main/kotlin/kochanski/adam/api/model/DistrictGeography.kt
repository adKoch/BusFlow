package kochanski.adam.api.model

import org.springframework.data.mongodb.core.mapping.Document

@Document
data class DistrictGeography(
        var districtName: String,
        var type: String,
        var coordinates: List<GeoPoint>) {
    fun getAsGeoJson(): String =
            "{\"type\":\"$type\", \"districtName\":$districtName\", \"coordinates\":[[[${coordinates.map { point -> "[" + point.latitude + "," + point.longitude + "]," }}]]]}"
}