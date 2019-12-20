package kochanski.adam.busflowpicker.model.repostiory_impls

import kochanski.adam.busflowpicker.model.VehicleLocationRepository
import kochanski.adam.busflowpicker.model.entities.VehicleLocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.time.Instant
import java.time.LocalDateTime

@Repository
class VehicleLocationMongoRepository(@Autowired var mongoTemplate: ReactiveMongoTemplate) : VehicleLocationRepository {
    override fun deleteAllInsertedBefore(date: LocalDateTime): Mono<Void> {
        return mongoTemplate.findAllAndRemove(getInsertedBeforeQuery(date),VehicleLocation::class.java).then()
    }

    override fun insert(vehicleLocations: List<VehicleLocation>): Mono<Void> {
        return mongoTemplate.insertAll(Mono.just(vehicleLocations),VehicleLocation::class.java).then()
    }

    private fun getInsertedBeforeQuery(date: LocalDateTime): Query {
        return Query(Criteria("timeRequested").lte(date))
    }
}