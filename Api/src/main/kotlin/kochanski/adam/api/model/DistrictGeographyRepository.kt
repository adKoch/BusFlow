package kochanski.adam.api.model

import kochanski.adam.api.model.entity.DistrictGeography
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface DistrictGeographyRepository : ReactiveMongoRepository<DistrictGeography, String>
