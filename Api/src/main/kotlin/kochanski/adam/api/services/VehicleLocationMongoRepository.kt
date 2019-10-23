package kochanski.adam.api.services

import java.time.LocalDateTime
import kochanski.adam.api.model.VehicleLocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class VehicleLocationMongoRepository(@Autowired val mongoTemplate: ReactiveMongoTemplate) : VehicleLocationRepository {

    override fun findAllRequestedInLastSeconds(seconds: Long, lines: List<String>): Flux<VehicleLocation> =
        mongoTemplate.find(getRequestedInLastTimeQuery(seconds, lines), VehicleLocation::class.java)

    override fun findAllRequestedInLastSeconds(seconds: Long): Flux<VehicleLocation> =
        mongoTemplate.find(getRequestedInLastTimeQuery(seconds), VehicleLocation::class.java)

    private fun getRequestedInLastTimeQuery(seconds: Long): Query =
                Query().addCriteria(Criteria().and("timeRequested").gte(LocalDateTime.now().minusSeconds(seconds)))

    private fun getRequestedInLastTimeQuery(seconds: Long, lines: List<String>): Query =
            getRequestedInLastTimeQuery(seconds).addCriteria(Criteria().and("line").`in`(lines))
}
