package kochanski.adam.busflowpicker.model.entities

import java.time.LocalDateTime
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("station")
data class Station(
    val latitude: Float,
    val longitude: Float,
    val streetId: String,
    val pool: String,
    val post: String,
    var poolName: String,
    var direction: String,
    var validFrom: LocalDateTime,
    var lines: List<String>?
)
