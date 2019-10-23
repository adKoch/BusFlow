package kochanski.adam.api.controller

import kochanski.adam.api.model.VehicleLocation
import kochanski.adam.api.services.VehicleLocationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/vehicle_location")
class VehicleLocationController(@Autowired val vehicleLocationService: VehicleLocationService) {

    @GetMapping
    fun getAllLastRequested(@RequestParam(required = false) lines: List<String>?): Flux<VehicleLocation> {
        return if (lines.isNullOrEmpty())
            vehicleLocationService.getLastRequestedVehicleLocations()
        else
            vehicleLocationService.getLastRequestedVehicleLocations(lines)
    }
}
