package kochanski.adam.api.services

import java.time.LocalDateTime
import kochanski.adam.api.model.Station
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
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
}
