package kochanski.adam.api.model.entity

import java.time.Instant
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document("vehicle_location")
data class VehicleLocation(var latitude: Float, var longitude: Float, var line: String, var brigade: String, var type: Int, var timeSent: LocalDateTime, var timeRequested: LocalDateTime)
