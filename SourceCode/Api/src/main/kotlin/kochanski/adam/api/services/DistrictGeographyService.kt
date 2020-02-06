package kochanski.adam.api.services

import kochanski.adam.api.model.DistrictGeographyRepository
import kochanski.adam.api.model.entity.DistrictGeography
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class DistrictGeographyService(@Autowired val districtGeographyRepository: DistrictGeographyRepository) {
    fun findAll(): Flux<DistrictGeography> =
            districtGeographyRepository.findAll()
}
