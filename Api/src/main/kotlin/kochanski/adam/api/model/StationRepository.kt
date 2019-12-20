package kochanski.adam.api.model

import kochanski.adam.api.model.entity.Station
import kochanski.adam.api.model.entity.statistic.StationStatistic
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface StationRepository {
    fun getAllActiveStationsByLines(lines: List<String>): Flux<Station>
}
