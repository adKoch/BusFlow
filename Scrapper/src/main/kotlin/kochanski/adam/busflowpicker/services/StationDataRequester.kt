package kochanski.adam.busflowpicker.services

import kochanski.adam.busflowpicker.model.entities.Station
import reactor.core.publisher.Flux

interface StationDataRequester {
    fun requestStations(): Flux<Station>
}
