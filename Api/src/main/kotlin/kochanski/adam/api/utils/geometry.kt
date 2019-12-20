package kochanski.adam.api.utils

import kochanski.adam.api.model.entity.GeoPoint

fun GeoPoint.isPointInsidePolygon(polygonPoints: List<GeoPoint>): Boolean {
    var i = 0
    var j = polygonPoints.size - 1
    var result = false
    while (i < polygonPoints.size) {
        if (polygonPoints[i].lat > lat != polygonPoints[j].lat > lat &&
                lon < (polygonPoints[j].lon - polygonPoints[i].lon) *
                (lat - polygonPoints[i].lat) / (polygonPoints[j].lat - polygonPoints[i].lat) + polygonPoints[i].lon) {
            result = !result
        }
        j = i++
    }
    return result
}

fun GeoPoint.isPointInsidePolygons(polygons: List<List<GeoPoint>>): Boolean {
    if (polygons.isEmpty()) return true
    var isInside = false
    polygons.forEach { polygonPoints ->
        if (!isInside && isPointInsidePolygon(polygonPoints)) {
            isInside = true
        }
    }
    return isInside
}

fun pointsFromCoordinates(latitudes: List<Float>, longitudes: List<Float>): List<GeoPoint> {
    if (latitudes.isNullOrEmpty() || longitudes.isNullOrEmpty() || latitudes.size != longitudes.size) return emptyList()
    val points: MutableList<GeoPoint> = ArrayList()
    for (i in latitudes.indices) {
        points += GeoPoint(latitudes[i], longitudes[i])
    }
    return points
}


