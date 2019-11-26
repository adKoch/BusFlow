package kochanski.adam.api.model

import java.time.LocalDateTime
import org.springframework.data.mongodb.core.mapping.Document

@Document("station")
data class Station(
    var latitude: Float,
    var longitude: Float,
    var streetId: String,
    var pool: String,
    var post: String,
    var poolName: String,
    var direction: String,
    var validFrom: LocalDateTime,
    var lines: List<String>?
)
