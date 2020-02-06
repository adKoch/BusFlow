package kochanski.adam.api.services

import java.time.LocalDateTime
import kochanski.adam.api.model.VehicleLocationRepository
import kochanski.adam.api.model.entity.GeoPoint
import kochanski.adam.api.model.entity.VehicleLocation
import kochanski.adam.api.model.entity.statistic.VehicleCountStatistic
import kochanski.adam.api.model.entity.statistic.VehiclesStatistic
import kochanski.adam.api.utils.isPointInsidePolygon
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class VehicleLocationService(@Autowired val vehicleLocationRepository: VehicleLocationRepository) {

    private val requestInterval: Long = 10L

    fun getLastRequestedVehicleLocations(lines: List<String>): Flux<VehicleLocation> = vehicleLocationRepository.findAllLastRequested(lines)

    fun getVehicleCountByTime(
        lines: List<String>?,
        polygons: List<GeoPoint>,
        start: LocalDateTime,
        end: LocalDateTime,
        secondsInterval: Long,
        maxLatitude: Float,
        minLatitude: Float,
        maxLongitude: Float,
        minLongitude: Float
    ): Flux<VehicleCountStatistic> {
        return if (lines.isNullOrEmpty())
            getVehicleStatistics(polygons, start, end, secondsInterval, requestInterval, maxLatitude, minLatitude, maxLongitude, minLongitude)
                    .map { stat -> VehicleCountStatistic(stat.start, stat.end, getVehicleCountFromVehicles(stat.vehicleCounts).toFloat()) }
        else
            getVehicleStatistics(lines, polygons, start, end, secondsInterval, requestInterval, maxLatitude, minLatitude, maxLongitude, minLongitude)
                    .map { stat -> VehicleCountStatistic(stat.start, stat.end, getVehicleCountFromVehicles(stat.vehicleCounts).toFloat()) }
    }

    fun getLineCountByTime(
        lines: List<String>?,
        polygonPoints: List<GeoPoint>,
        start: LocalDateTime,
        end: LocalDateTime,
        secondsInterval: Long,
        maxLatitude: Float,
        minLatitude: Float,
        maxLongitude: Float,
        minLongitude: Float
    ): Flux<VehicleCountStatistic> {
        return if (lines.isNullOrEmpty())
            getVehicleStatistics(polygonPoints, start, end, secondsInterval, requestInterval, maxLatitude, minLatitude, maxLongitude, minLongitude)
                    .map { stat -> VehicleCountStatistic(stat.start, stat.end, getVehicleLineCountFromVehicles(stat.vehicleCounts).toFloat()) }
        else
            getVehicleStatistics(lines, polygonPoints, start, end, secondsInterval, requestInterval, maxLatitude, minLatitude, maxLongitude, minLongitude)
                    .map { stat -> VehicleCountStatistic(stat.start, stat.end, getVehicleLineCountFromVehicles(stat.vehicleCounts).toFloat()) }
    }

    private fun getVehicleStatistics(
        lines: List<String>,
        polygonPoints: List<GeoPoint>,
        start: LocalDateTime,
        end: LocalDateTime,
        secondsInterval: Long,
        requestInterval: Long,
        maxLatitude: Float,
        minLatitude: Float,
        maxLongitude: Float,
        minLongitude: Float
    ): Flux<VehiclesStatistic> {
        return vehicleLocationRepository.findAllByLinesInTimeInRect(lines, start, end, maxLatitude, minLatitude, maxLongitude, minLongitude)
                .filter { GeoPoint(lat = it.latitude, lon = it.longitude).isPointInsidePolygon(polygonPoints) }
                .collectList()
                .flatMapIterable { locationList -> vehiclesByTime(start, end, secondsInterval, requestInterval, locationList) }
    }

    private fun getVehicleStatistics(
        polygonPoints: List<GeoPoint>,
        start: LocalDateTime,
        end: LocalDateTime,
        secondsInterval: Long,
        requestInterval: Long,
        maxLatitude: Float,
        minLatitude: Float,
        maxLongitude: Float,
        minLongitude: Float
    ): Flux<VehiclesStatistic> {
        return vehicleLocationRepository.findAllInTimeInRect(start, end, maxLatitude, minLatitude, maxLongitude, minLongitude)
                .filter { GeoPoint(lat = it.latitude, lon = it.longitude).isPointInsidePolygon(polygonPoints) }
                .collectList()
                .flatMapIterable { locationList -> vehiclesByTime(start, end, secondsInterval, requestInterval, locationList) }
    }

    private fun getDateSequence(start: LocalDateTime, end: LocalDateTime, secondsInterval: Long): List<Pair<LocalDateTime, LocalDateTime>> {
        val sequence: MutableList<Pair<LocalDateTime, LocalDateTime>> = ArrayList()
        var startIt = start
        var nextIt: LocalDateTime
        while (startIt.isBefore(end)) {
            nextIt = if (startIt.plusSeconds(secondsInterval) > end) end else startIt.plusSeconds(secondsInterval)
            sequence += Pair(startIt, nextIt)
            startIt = nextIt
        }
        return sequence
    }

    private fun vehiclesByTime(start: LocalDateTime, end: LocalDateTime, secondsInterval: Long, requestInterval: Long, vehicles: List<VehicleLocation>): List<VehiclesStatistic> {
        return getDateSequence(start, end, secondsInterval)
                .map { dateRange ->
                    VehiclesStatistic(
                            dateRange.first,
                            dateRange.second,
                            mapLocationsToLineAvgCountMap(
                                    vehicles.filter { it.timeSent.isBefore(dateRange.second) && it.timeSent.isAfter(dateRange.first) }, secondsInterval, requestInterval))
                }
    }

    private fun getVehicleCountFromVehicles(vehiclesByLine: Map<String, Long>): Long {
        var count: Long = 0
        vehiclesByLine.forEach { count += it.value }
        return count
    }

    private fun getVehicleLineCountFromVehicles(vehiclesByLine: Map<String, Long>): Long {
        return vehiclesByLine.size.toLong()
    }

    private fun mapLocationsToLineAvgCountMap(locations: List<VehicleLocation>, secondsInterval: Long, requestInterval: Long): Map<String, Long> {
        val lineCounts: MutableMap<String, Long> = HashMap()
        var line: String
        var currentCount: Long
        locations.forEach { location ->
            line = location.line
            if (lineCounts.containsKey(line)) {
                currentCount = lineCounts[line]!!
                lineCounts.replace(line, currentCount.plus(1))
            } else {
                lineCounts[line] = 1
            }
        }
        return lineCounts.mapValues { it.value * requestInterval / secondsInterval }
    }
}
