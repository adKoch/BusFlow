package kochanski.adam.busflowpicker.services

import kochanski.adam.busflowpicker.model.VehicleLocationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Component
@Transactional
class SchedulerService(@Autowired val requesterService: RequesterService, @Autowired val vehicleLocationRepository: VehicleLocationRepository) {

    @Scheduled(cron = "*/10 * * * * *")
    fun scheduleInsert() {
        requesterService.requestVehicles().collectList().subscribe { vehicleLocationRepository.insert(it).log().subscribe() }
    }

    @Scheduled(cron = "1 0 */1 * * *")
    fun scheduleDelete() {
        vehicleLocationRepository.deleteAll().log().then<Unit>(Mono.justOrEmpty(scheduleInsert())).subscribe()
    }
}
