package kochanski.adam.busflowpicker.services.Requesters

import kochanski.adam.busflowpicker.model.entities.VehicleLocation
import reactor.core.publisher.Flux

interface VehicleDataRequester {
    fun requestVehicles(): Flux<VehicleLocation>
}
