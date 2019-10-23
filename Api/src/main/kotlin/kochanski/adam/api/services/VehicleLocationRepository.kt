package kochanski.adam.api.services

import kochanski.adam.api.model.VehicleLocation
import reactor.core.publisher.Flux

interface VehicleLocationRepository {

    fun findAllRequestedInLastSeconds(seconds: Long): Flux<VehicleLocation>

    fun findAllRequestedInLastSeconds(seconds: Long, lines: List<String>): Flux<VehicleLocation>
}
