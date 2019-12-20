package kochanski.adam.api.services.repository_implementations

import java.time.Instant
import kochanski.adam.api.model.VehicleLocationRepository
import kochanski.adam.api.model.entity.VehicleLocation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.*
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.time.LocalDateTime

@Repository
class VehicleLocationMongoRepository(@Autowired val mongoTemplate: ReactiveMongoTemplate) : VehicleLocationRepository {

    override fun findAllLastRequested(lines: List<String>): Flux<VehicleLocation> =
            mongoTemplate.aggregate(getAllLastNotedAggregation(lines), VehicleLocation::class.java)

    private fun getAllLastNotedAggregation(lines:List<String>): TypedAggregation<VehicleLocation> {
        return newAggregation(VehicleLocation::class.java,
                match(Criteria("line").`in`(lines)
                        .and("timeRequested").gte(LocalDateTime.now().minusSeconds(40L))),
                group("brigade","line")
                        .max("timeSent").`as`("timeSent")
                        .first("line").`as`("line")
                        .first("type").`as`("type")
                        .first("brigade").`as`("brigade")
                        .first("brigade").`as`("brigade")
                        .first("latitude").`as`("latitude")
                        .first("longitude").`as`("longitude")
                        .first("timeRequested").`as`("timeRequested")
        )
    }

    override fun findAllByLinesInTimeInRect(lines: List<String>,
                                            start: LocalDateTime,
                                            end: LocalDateTime,
                                            maxLatitude: Float,
                                            minLatitude: Float,
                                            maxLongitude: Float,
                                            minLongitude: Float): Flux<VehicleLocation> =
            mongoTemplate.find(getRequestedByLinesInTimeQueryInRect(
                    lines, start, end, maxLatitude, minLatitude, maxLongitude, minLongitude),
                    VehicleLocation::class.java)

    override fun findAllInTimeInRect(start: LocalDateTime,
                                     end: LocalDateTime,
                                     maxLatitude: Float,
                                     minLatitude: Float,
                                     maxLongitude: Float,
                                     minLongitude: Float): Flux<VehicleLocation> =
            mongoTemplate.find(getRequestedInTimeQuery(
                    start, end, maxLatitude, minLatitude, maxLongitude, minLongitude),
                    VehicleLocation::class.java)

    private fun getRequestedByLinesInTimeQueryInRect(lines: List<String>,
                                                     start: LocalDateTime,
                                                     end: LocalDateTime,
                                                     maxLatitude: Float,
                                                     minLatitude: Float,
                                                     maxLongitude: Float,
                                                     minLongitude: Float): Query =
            Query().addCriteria(Criteria()
                    .and("latitude").lte(maxLatitude).gte(minLatitude)
                    .and("longitude").lte(maxLongitude).gte(minLongitude)
                    .and("line").`in`(lines)
                    .and("timeSent").gt(start)
                    .lte(end))

    private fun getRequestedInTimeQuery(start: LocalDateTime,
                                        end: LocalDateTime,
                                        maxLatitude: Float,
                                        minLatitude: Float,
                                        maxLongitude: Float,
                                        minLongitude: Float): Query =
            Query().addCriteria(Criteria()
                    .and("latitude").lte(maxLatitude).gte(minLatitude)
                    .and("longitude").lte(maxLongitude).gte(minLongitude)
                    .and("timeSent").gt(start)
                    .lte(end))
}
