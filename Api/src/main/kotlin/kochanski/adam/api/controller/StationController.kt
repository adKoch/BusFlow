package kochanski.adam.api.controller

import kochanski.adam.api.model.Station
import kochanski.adam.api.services.StationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/station")
@CrossOrigin("http://localhost:8080")
class StationController(@Autowired val stationService: StationService) {

    @GetMapping
    fun getAllActiveStations(@RequestParam(required = false) lines: List<String>?): Flux<Station> {
        return if (lines.isNullOrEmpty())
            return Flux.empty()
        else stationService.getAllActiveStations(lines)
    }
}
