package kochanski.adam.busflowpicker.model

import kochanski.adam.busflowpicker.model.entities.VehicleLocation
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface VehicleLocationRepository : ReactiveMongoRepository<VehicleLocation, String>
