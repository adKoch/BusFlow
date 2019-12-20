package kochanski.adam.busflowpicker.model

import kochanski.adam.busflowpicker.model.entities.VehicleLocation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDateTime

interface VehicleLocationRepository {
    fun deleteAllInsertedBefore(date: LocalDateTime): Mono<Void>
    fun insert(vehicleLocations: List<VehicleLocation>): Mono<Void>
}
