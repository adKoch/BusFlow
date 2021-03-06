package kochanski.adam.api.services.repository_implementations

import java.time.LocalDateTime
import kochanski.adam.api.model.StationRepository
import kochanski.adam.api.model.entity.Station
import kochanski.adam.api.model.entity.statistic.StationStatistic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation.group
import org.springframework.data.mongodb.core.aggregation.Aggregation.match
import org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation
import org.springframework.data.mongodb.core.aggregation.Aggregation.project
import org.springframework.data.mongodb.core.aggregation.Aggregation.unwind
import org.springframework.data.mongodb.core.aggregation.TypedAggregation
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class StationMongoRepository(@Autowired val mongoTemplate: ReactiveMongoTemplate) : StationRepository {

    override fun getAllActiveStationsByLines(lines: List<String>): Flux<Station> {
        return mongoTemplate.find(getActiveStationsByLinesQuery(lines), Station::class.java)
    }

    private fun getActiveStationsByLinesQuery(lines: List<String>): Query =
            Query().addCriteria(Criteria()
                    .and("validFrom").lte(LocalDateTime.now())
                    .and("lines").`in`(lines))

    /*
        override fun getStationCountForLines(lines: List<String>): Flux<StationStatistic> {
            return mongoTemplate.aggregate(getStationCountForLinesAggregation(lines),StationStatistic::class.java)
        }
    */

    private fun getStationCountForLinesAggregation(lines: List<String>): TypedAggregation<StationStatistic> {
        return newAggregation(StationStatistic::class.java,
                unwind("lines", false),
                match(Criteria("lines").`in`(lines)),
                group("lines").count().`as`("count"),
                project().and("lines").`as`("line").and("count").`as`("count")
        )
    }
}
