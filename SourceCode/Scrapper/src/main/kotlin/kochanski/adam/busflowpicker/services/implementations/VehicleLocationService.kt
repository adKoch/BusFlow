package kochanski.adam.busflowpicker.services.implementations

import java.time.LocalDateTime
import kochanski.adam.busflowpicker.model.VehicleLocationRepository
import kochanski.adam.busflowpicker.services.Requesters.VehicleDataRequester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class VehicleLocationService(@Autowired val vehicleLocationRepository: VehicleLocationRepository, @Autowired val vehicleDataRequester: VehicleDataRequester) {

    fun loadNewData(): Flux<Void> {
        return vehicleDataRequester.requestVehicles()
                .collectList()
                .flatMapMany { vehicleLocationRepository.insert(it) }
    }
    fun deleteOlderThan(date: LocalDateTime): Mono<Void> {
        return vehicleLocationRepository.deleteAllInsertedBefore(date)
    }
}
