package kochanski.adam.busflowpicker.model

import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.UpdateResult
import kochanski.adam.busflowpicker.model.entities.Station
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface StationRepository {
    fun deleteAll(): Mono<Void>

    fun findAll(): Flux<Station>

    fun insert(station: Station): Mono<Station>

    fun insert(stations: Collection<Station>): Flux<Station>

    fun findSomeStationsWithoutLines(limit:Int): Flux<Station>

    fun updateWithLines(station: Station, lines:List<String>): Mono<Station>
}
