package kochanski.adam.busflowpicker.model

import kochanski.adam.busflowpicker.model.entities.DistrictGeography
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface DistrictGeographyRepository : ReactiveMongoRepository<DistrictGeography, String>
