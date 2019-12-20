package kochanski.adam.api.model

import java.time.Instant
import kochanski.adam.api.model.entity.VehicleLocation
import reactor.core.publisher.Flux
import java.time.LocalDateTime

interface VehicleLocationRepository {


    fun findAllLastRequested(lines: List<String>): Flux<VehicleLocation>

    fun findAllByLinesInTimeInRect(lines: List<String>,
                                   start: LocalDateTime,
                                   end: LocalDateTime,
                                   maxLatitude: Float,
                                   minLatitude: Float,
                                   maxLongitude: Float,
                                   minLongitude: Float): Flux<VehicleLocation>

    fun findAllInTimeInRect(start: LocalDateTime,
                      end: LocalDateTime,
                      maxLatitude: Float,
                      minLatitude: Float,
                      maxLongitude: Float,
                      minLongitude: Float): Flux<VehicleLocation>
}
