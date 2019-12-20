package kochanski.adam.api.services

import kochanski.adam.api.model.StationRepository
import kochanski.adam.api.model.entity.GeoPoint
import kochanski.adam.api.model.entity.Station
import kochanski.adam.api.model.entity.statistic.StationStatistic
import kochanski.adam.api.utils.isPointInsidePolygon
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class StationService(@Autowired val stationRepository: StationRepository) {

    fun getAllActiveStations(lines: List<String>): Flux<Station> = stationRepository.getAllActiveStationsByLines(lines)

}
