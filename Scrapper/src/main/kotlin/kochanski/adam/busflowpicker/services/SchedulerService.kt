package kochanski.adam.busflowpicker.services

import kochanski.adam.busflowpicker.model.StationRepository
import kochanski.adam.busflowpicker.model.VehicleLocationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Component
class SchedulerService(
    @Autowired val vehicleDataRequester: VehicleDataRequester,
    @Autowired val stationDataRequester: StationDataRequester,
    @Autowired val stationLinesDataRequester: StationLinesDataRequester,
    @Autowired val vehicleLocationRepository: VehicleLocationRepository,
    @Autowired val stationRepository: StationRepository
) {
    private var stationsBatch = 10

    @Transactional
    @Scheduled(cron = "*/10 * * * * *")
    fun scheduleInsert() {
        vehicleDataRequester.requestVehicles()
                .onErrorStop()
                .collectList()
                .subscribe {
                    vehicleLocationRepository.insert(it)
                            .log()
                            .subscribe()
                }
    }

    @Transactional
    @Scheduled(cron = "1 0 */1 * * *")
    fun scheduleDeleteThenInsert() {
        vehicleLocationRepository.deleteAll()
                .log()
                .then<Unit>(Mono.justOrEmpty(scheduleInsert()))
                .subscribe()
    }

    @Transactional
    // @Scheduled(cron = "1 0 0 1 */1 *")
    fun scheduleStationUpdate() {
        stationRepository.deleteAll()
                .log()
                .then(Mono.just(stationDataRequester.requestStations()
                        .onErrorStop()
                        .log()
                        .collectList()
                        .subscribe {
                            stationRepository.insert(it)
                                    .log()
                                    .subscribe()
                        }))
                .subscribe { }
    }

    @Scheduled(cron = "*/3 * * * * *")
    fun scheduleStationLinesUpdate() {
        stationRepository.findSomeStationsWithoutLines(stationsBatch)
                .onErrorStop()
                .log()
                .subscribe { station ->
                    stationLinesDataRequester.requestLines(station)
                            .log()
                            .collectList()
                            .log()
                            .subscribe {
                                if (it.isEmpty()) it.add("brak")
                                stationRepository.updateWithLines(station, it).log().subscribe()
                            }
                }
    }
}
