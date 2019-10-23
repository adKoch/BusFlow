package kochanski.adam.busflowpicker.services

import kochanski.adam.busflowpicker.model.VehicleLocation
import reactor.core.publisher.Flux

interface RequesterService {
    fun requestVehicles(): Flux<VehicleLocation>
}
