package kochanski.adam.busflowpicker.services.implementations

import com.google.gson.Gson
import com.google.gson.JsonObject
import kochanski.adam.busflowpicker.model.entities.Station
import kochanski.adam.busflowpicker.model.utils.normaliseJson
import kochanski.adam.busflowpicker.services.StationLinesDataRequester
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class StationLinesDataRequesterImpl : StationLinesDataRequester {

    private val gsonInstance = Gson()

    private val resourceId: String = "88cd555f-6f31-43ca-9de4-66c479ad5942"
    private val RESOURCE_ID_NAME = "id"

    private val API_KEY_NAME = "apikey"
    private var apiKey: String = System.getenv("api.warszawa.apikey")

    private val BUS_STOP_ID_NAME = "busstopId"
    private val BUS_STOP_NR_NAME = "busstopNr"

    private var webClient: WebClient = WebClient.builder()
            .baseUrl("https://api.um.warszawa.pl/api/action/dbtimetable_get/")
            .build()

    override fun requestLines(station: Station): Flux<String> {
        return webClient.get()
                .uri {
                    it.queryParam(RESOURCE_ID_NAME, resourceId)
                            .queryParam(API_KEY_NAME, apiKey)
                            .queryParam(BUS_STOP_ID_NAME, station.pool)
                            .queryParam(BUS_STOP_NR_NAME, station.post)
                            .build()
                }.retrieve()
                .bodyToMono(String::class.java)
                .linesFromJson()
    }

    private fun Mono<String>.linesFromJson(): Flux<String> =
            map { response ->
                gsonInstance.fromJson(response, JsonObject::class.java)
                        .get("result").asJsonArray
                        .map { normaliseJson(it.asJsonObject) }
            }
                    .flatMapMany { Flux.fromIterable(it) }
                    .map {
                        it.get("linia").asString
                    }
}
