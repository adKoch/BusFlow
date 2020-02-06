package kochanski.adam.busflowpicker.services.Requesters

import kochanski.adam.busflowpicker.model.entities.Station
import reactor.core.publisher.Flux

interface StationLinesDataRequester {
    fun requestLines(station: Station): Flux<String>
}
