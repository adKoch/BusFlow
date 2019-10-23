package kochanski.adam.busflowpicker.model

import com.google.gson.JsonObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.springframework.data.mongodb.core.mapping.Document

@Document("vehicle_location")
data class VehicleLocation(var latitude: Float, var longitude: Float, var line: String, var brigade: String, var type: Int, var timeSent: LocalDateTime, var timeRequested: LocalDateTime) {
companion object VehicleCreator {
    private var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun vehicleMappedFromJsonObject(jsonObject: JsonObject, ofType: Int, ofTime: LocalDateTime): VehicleLocation {

        return VehicleLocation(latitude = jsonObject.get("Lat").asFloat,
                longitude = jsonObject.get("Lon").asFloat,
                line = jsonObject.get("Lines").asString,
                brigade = jsonObject.get("Brigade").asString,
                type = ofType,
                timeSent = LocalDateTime.parse(jsonObject.get("Time").asString, formatter),
                timeRequested = ofTime)
    }
}
}
