package kochanski.adam.busflowpicker.model

import java.time.LocalDateTime
import kochanski.adam.busflowpicker.model.entities.VehicleLocation
import reactor.core.publisher.Mono

interface VehicleLocationRepository {
    fun deleteAllInsertedBefore(date: LocalDateTime): Mono<Void>
    fun insert(vehicleLocations: List<VehicleLocation>): Mono<Void>
}
