package kochanski.adam.api.controller

import kochanski.adam.api.model.VehicleLocation
import kochanski.adam.api.services.VehicleLocationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/vehicle_location")
@CrossOrigin("http://localhost:8080")
class VehicleLocationController(@Autowired val vehicleLocationService: VehicleLocationService) {

    @GetMapping
    fun getAllLastRequested(@RequestParam(required = false) lines: List<String>?): Flux<VehicleLocation> {
        return if (lines.isNullOrEmpty())
            vehicleLocationService.getLastRequestedVehicleLocations().log()
        else
            vehicleLocationService.getLastRequestedVehicleLocations(lines).log()
    }
}
