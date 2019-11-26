package kochanski.adam.api.services

import kochanski.adam.api.model.Station
import reactor.core.publisher.Flux

interface StationRepository {
    fun getAllActiveStationsByLines(lines: List<String>): Flux<Station>
}
