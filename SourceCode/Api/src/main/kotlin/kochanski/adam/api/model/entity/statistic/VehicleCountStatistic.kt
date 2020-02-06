package kochanski.adam.api.model.entity.statistic

import java.time.LocalDateTime

data class VehicleCountStatistic(var start: LocalDateTime, var end: LocalDateTime, var count: Float)
