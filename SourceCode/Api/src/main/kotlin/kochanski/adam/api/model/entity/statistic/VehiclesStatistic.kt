package kochanski.adam.api.model.entity.statistic

import java.time.LocalDateTime

data class VehiclesStatistic(
    var start: LocalDateTime,
    var end: LocalDateTime,
    var vehicleCounts: Map<String, Long>
)
