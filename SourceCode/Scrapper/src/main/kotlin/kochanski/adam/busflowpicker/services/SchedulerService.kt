package kochanski.adam.busflowpicker.services

import java.time.Duration
import java.time.LocalDateTime
import javax.annotation.PostConstruct
import kochanski.adam.busflowpicker.services.implementations.StationService
import kochanski.adam.busflowpicker.services.implementations.VehicleLocationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class SchedulerService(
    @Autowired val vehicleLocationService: VehicleLocationService,
    @Autowired val stationService: StationService
) {
    private val stationsBatch = 10
    private val stationsHoldThreshold = LocalDateTime.now().minusDays(2L)
    private val loadingNewDataInterval = Duration.ofSeconds(10L)
    private val deleteInterval = Duration.ofDays(1L)
    private val stationUpdateInterval = Duration.ofDays(30L)
    private val stationLinesUpdateInterval = Duration.ofSeconds(3L)

    @PostConstruct
    fun scheduleAll() {
        scheduleLoadingNewData()
        scheduleDeleteOlderThanThreshold()
        // scheduleStationUpdate()
        scheduleStationLinesUpdate()
    }

    private fun scheduleLoadingNewData() {
        vehicleLocationService.loadNewData()
                .log()
                .delaySubscription(loadingNewDataInterval)
                .retry()
                .repeat()
                .subscribe()
    }

    private fun scheduleDeleteOlderThanThreshold() {
        vehicleLocationService.deleteOlderThan(stationsHoldThreshold)
                .log()
                .delaySubscription(deleteInterval)
                .repeat()
                .subscribe()
    }

    private fun scheduleStationUpdate() {
        stationService.updateStations()
                .log()
                .delaySubscription(stationUpdateInterval)
                .repeat()
                .subscribe()
    }

    private fun scheduleStationLinesUpdate() {
        stationService.updateLinesForStations(stationsBatch)
                .log()
                .delaySubscription(stationLinesUpdateInterval)
                .repeat()
                .subscribe()
    }
}
