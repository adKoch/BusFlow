package kochanski.adam.busflowpicker.model.utils

import com.google.gson.JsonObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kochanski.adam.busflowpicker.model.entities.Station
import kochanski.adam.busflowpicker.model.entities.VehicleLocation

const val warsawApiVehicleDateTimePattern = "yyyy-MM-dd HH:mm:ss"
const val warsawApiStationDateTimePattern = "yyyy-MM-dd HH:mm:ss.s"

fun vehicleLocationMappedFromJsonObject(jsonObject: JsonObject, ofType: Int, ofTime: LocalDateTime): VehicleLocation {
    return VehicleLocation(latitude = jsonObject.get("Lat").asFloat,
            longitude = jsonObject.get("Lon").asFloat,
            line = jsonObject.get("Lines").asString,
            brigade = jsonObject.get("Brigade").asString,
            type = ofType,
            timeSent = LocalDateTime.parse(jsonObject.get("Time").asString, DateTimeFormatter.ofPattern(warsawApiVehicleDateTimePattern)),
            timeRequested = ofTime)
}

fun stationMappedFromJsonObject(jsonObject: JsonObject): Station {
    return Station(latitude = jsonObject.get("szer_geo").asFloat,
            longitude = jsonObject.get("dlug_geo").asFloat,
            pool = jsonObject.get("zespol").asString,
            streetId = jsonObject.get("id_ulicy").asString,
            poolName = jsonObject.get("nazwa_zespolu").asString,
            post = jsonObject.get("slupek").asString,
            direction = jsonObject.get("kierunek").asString,
            validFrom = LocalDateTime.parse(jsonObject.get("obowiazuje_od").asString, DateTimeFormatter.ofPattern(warsawApiStationDateTimePattern)),
            lines = null)
}

fun normaliseJson(valuesJsonObject: JsonObject): JsonObject {
    val newJsonObject = JsonObject()
    valuesJsonObject.get("values").asJsonArray.forEach { jsonElement ->
        newJsonObject.addProperty(jsonElement.asJsonObject.get("key").asString, jsonElement.asJsonObject.get("value").asString)
    }
    return newJsonObject
}
