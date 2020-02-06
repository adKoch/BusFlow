package kochanski.adam.api.model.entity.statistic

import org.springframework.data.mongodb.core.mapping.Document

@Document("Station")
data class StationStatistic(
    var line: String,
    var count: Float
)
