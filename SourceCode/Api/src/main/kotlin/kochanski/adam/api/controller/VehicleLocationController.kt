package kochanski.adam.api.controller

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kochanski.adam.api.model.entity.VehicleLocation
import kochanski.adam.api.model.entity.statistic.VehicleCountStatistic
import kochanski.adam.api.services.VehicleLocationService
import kochanski.adam.api.utils.maxFloat
import kochanski.adam.api.utils.minFloat
import kochanski.adam.api.utils.pointsFromCoordinates
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/vehicle_location")
class VehicleLocationController(@Autowired val vehicleLocationService: VehicleLocationService) {
    private val dateTimePattern = "yyyy-MM-dd HH:mm:ss"

    @GetMapping
    fun getAllLastRequested(@RequestParam lines: List<String>): Flux<VehicleLocation> {
        return vehicleLocationService.getLastRequestedVehicleLocations(lines).log()
    }

    @GetMapping("/count_statistics")
    fun getVehicleCountStatistics(
        @RequestParam(required = false) lines: List<String>?,
        @RequestParam polygonLatitudes: List<Float>,
        @RequestParam polygonLongitudes: List<Float>,
        @RequestParam start: String,
        @RequestParam end: String,
        @RequestParam interval: Long
    ): Flux<VehicleCountStatistic> {
        val points = pointsFromCoordinates(polygonLatitudes, polygonLongitudes)
        val maxLatitude = maxFloat(polygonLatitudes)
        val minLatitude = minFloat(polygonLatitudes)
        val maxLongitude = maxFloat(polygonLongitudes)
        val minLongitude = minFloat(polygonLongitudes)
        return vehicleLocationService.getVehicleCountByTime(lines,
                points,
                LocalDateTime.parse(start, DateTimeFormatter.ofPattern(dateTimePattern)),
                LocalDateTime.parse(end, DateTimeFormatter.ofPattern(dateTimePattern)),
                interval,
                maxLatitude,
                minLatitude,
                maxLongitude,
                minLongitude)
    }

    @GetMapping("/line_count_statistics")
    fun getVehicleLineCountStatistics(
        @RequestParam(required = false) lines: List<String>?,
        @RequestParam polygonLatitudes: List<Float>,
        @RequestParam polygonLongitudes: List<Float>,
        @RequestParam start: String,
        @RequestParam end: String,
        @RequestParam interval: Long
    ): Flux<VehicleCountStatistic> {
        val points = pointsFromCoordinates(polygonLatitudes, polygonLongitudes)
        val maxLatitude = maxFloat(polygonLatitudes)
        val minLatitude = minFloat(polygonLatitudes)
        val maxLongitude = maxFloat(polygonLongitudes)
        val minLongitude = minFloat(polygonLongitudes)
        return vehicleLocationService.getLineCountByTime(
                lines,
                points,
                LocalDateTime.parse(start, DateTimeFormatter.ofPattern(dateTimePattern)),
                LocalDateTime.parse(end, DateTimeFormatter.ofPattern(dateTimePattern)),
                interval,
                maxLatitude,
                minLatitude,
                maxLongitude,
                minLongitude)
    }
}
