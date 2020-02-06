package kochanski.adam.busflowpicker.model.entities

import java.time.LocalDateTime
import org.springframework.data.mongodb.core.mapping.Document

@Document("vehicle_location")
data class VehicleLocation(var latitude: Float, var longitude: Float, var line: String, var brigade: String, var type: Int, var timeSent: LocalDateTime, var timeRequested: LocalDateTime)
