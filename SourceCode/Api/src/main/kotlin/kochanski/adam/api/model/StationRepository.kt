package kochanski.adam.api.model

import kochanski.adam.api.model.entity.Station
import reactor.core.publisher.Flux

interface StationRepository {
    fun getAllActiveStationsByLines(lines: List<String>): Flux<Station>
}
