package kochanski.adam.api.model

import java.time.Instant
import org.springframework.data.mongodb.core.mapping.Document

@Document("vehicle_location")
data class VehicleLocation(var latitude: Float, var longitude: Float, var line: String, var brigade: String, var type: Int, var timeSent: Instant, var timeRequested: Instant)
