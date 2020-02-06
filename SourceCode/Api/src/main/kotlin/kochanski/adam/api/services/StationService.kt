package kochanski.adam.api.services

import kochanski.adam.api.model.StationRepository
import kochanski.adam.api.model.entity.Station
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class StationService(@Autowired val stationRepository: StationRepository) {

    fun getAllActiveStations(lines: List<String>): Flux<Station> = stationRepository.getAllActiveStationsByLines(lines)
}
