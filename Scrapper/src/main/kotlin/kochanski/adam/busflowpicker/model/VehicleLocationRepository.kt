package kochanski.adam.busflowpicker.model

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface VehicleLocationRepository : ReactiveMongoRepository<VehicleLocation, String>
