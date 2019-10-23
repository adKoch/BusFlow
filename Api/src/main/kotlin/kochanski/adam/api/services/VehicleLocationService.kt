package kochanski.adam.api.services

import kochanski.adam.api.model.VehicleLocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class VehicleLocationService(@Autowired val vehicleLocationRepository: VehicleLocationRepository) {

    private val requestInterval: Long = 10L

    fun getLastRequestedVehicleLocations(): Flux<VehicleLocation> = vehicleLocationRepository.findAllRequestedInLastSeconds(requestInterval)

    fun getLastRequestedVehicleLocations(lines: List<String>): Flux<VehicleLocation> = vehicleLocationRepository.findAllRequestedInLastSeconds(requestInterval, lines)
}
