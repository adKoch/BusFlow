package kochanski.adam.busflowpicker.services

import kochanski.adam.busflowpicker.model.VehicleLocationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class FlowService(@Autowired val requesterService: RequesterService, @Autowired val vehicleLocationRepository: VehicleLocationRepository) {

    @Scheduled(cron = "*/10 * * * * *")
    fun schedule() {
        requesterService.requestVehicles().collectList().subscribe { vehicleLocationRepository.insert(it).log().subscribe() }
    }
}
