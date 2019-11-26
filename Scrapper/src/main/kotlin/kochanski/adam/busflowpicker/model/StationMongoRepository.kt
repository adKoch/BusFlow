package kochanski.adam.busflowpicker.model

import com.mongodb.client.result.UpdateResult
import kochanski.adam.busflowpicker.model.entities.Station
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class StationMongoRepository(@Autowired val mongoTemplate: ReactiveMongoTemplate) : StationRepository {

    override fun deleteAll(): Mono<Void> {
        return mongoTemplate.dropCollection(Station::class.java)
    }

    override fun findAll(): Flux<Station> {
        return mongoTemplate.findAll(Station::class.java)
    }

    override fun insert(station: Station): Mono<Station> {
        return mongoTemplate.insert(station)
    }

    override fun insert(stations: Collection<Station>): Flux<Station> {
        return mongoTemplate.insertAll(stations)
    }

    override fun findSomeStationsWithoutLines(limit: Int): Flux<Station> {
        return mongoTemplate.find<Station>(getNullLinesLimitQuery(limit), Station::class.java)
    }

    override fun updateWithLines(station: Station, lines:List<String>): Mono<Station> {
        return mongoTemplate.findAndModify(getFindStationQuery(station), getStationLinesUpdate(lines), Station::class.java)
    }

    private fun getNullLinesLimitQuery(limit: Int): Query {
        return Query().addCriteria(Criteria()
                .and("lines").exists(false))
                .limit(limit)
    }

    private fun getFindStationQuery(station: Station): Query {
        return Query().addCriteria(Criteria()
                .and("streetId").`is`(station.streetId)
                .and("post").`is`(station.post)
                .and("pool").`is`(station.pool)
                .and("validFrom").`is`(station.validFrom))

    }

    private fun getStationLinesUpdate(lines: List<String>): Update {
        return Update().set("lines", lines)
    }
}