package kochanski.adam.api.controller

import kochanski.adam.api.model.entity.Station
import kochanski.adam.api.services.StationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/station")
class StationController(@Autowired val stationService: StationService) {

    @GetMapping
    fun getAllActiveStations(@RequestParam(required = false) lines: List<String>?): Flux<Station> {
        return if (lines.isNullOrEmpty())
            return Flux.empty()
        else stationService.getAllActiveStations(lines)
    }
}
