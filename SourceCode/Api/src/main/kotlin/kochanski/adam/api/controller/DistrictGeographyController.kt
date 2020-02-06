package kochanski.adam.api.controller

import kochanski.adam.api.model.entity.DistrictGeography
import kochanski.adam.api.services.DistrictGeographyService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/district_geography")
class DistrictGeographyController(@Autowired val districtGeographyService: DistrictGeographyService) {

    @GetMapping
    fun findAll(): Flux<DistrictGeography> = districtGeographyService.findAll()
}
