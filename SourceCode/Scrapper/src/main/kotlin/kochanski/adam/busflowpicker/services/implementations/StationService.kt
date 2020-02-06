package kochanski.adam.busflowpicker.services.implementations

import kochanski.adam.busflowpicker.model.StationRepository
import kochanski.adam.busflowpicker.model.entities.Station
import kochanski.adam.busflowpicker.services.Requesters.StationDataRequester
import kochanski.adam.busflowpicker.services.Requesters.StationLinesDataRequester
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class StationService(
    @Autowired val stationDataRequester: StationDataRequester,
    @Autowired val stationLinesDataRequester: StationLinesDataRequester,
    @Autowired val stationRepository: StationRepository
) {
    @Transactional
    fun updateStations(): Mono<Void> {
        return stationRepository.deleteAll()
                .then<Void>(insertNewStations())
    }

    private fun insertNewStations(): Mono<Void> {
        return stationDataRequester.requestStations()
                .collectList()
                .flatMapMany {
                    stationRepository.insert(it)
                }.then()
    }

    fun updateLinesForStations(stationsBatch: Int): Flux<Station> {
        return stationRepository.findSomeStationsWithoutLines(stationsBatch)
                .onErrorStop()
                .flatMap { updateLinesForStation(it) }
                .flatMap { stationRepository.updateWithLines(it, it.lines!!) }
    }

    private fun updateLinesForStation(station: Station): Mono<Station> {
        return stationLinesDataRequester.requestLines(station)
                .collectList()
                .map { lines -> stationWithLines(station, lines) }
    }

    private fun stationWithLines(station: Station, lines: List<String>): Station {
        return Station(
                station.latitude,
                station.longitude,
                station.streetId,
                station.pool,
                station.post,
                station.poolName,
                station.direction,
                station.validFrom,
                lines)
    }
}
