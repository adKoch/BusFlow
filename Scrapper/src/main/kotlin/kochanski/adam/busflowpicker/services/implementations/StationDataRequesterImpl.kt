package kochanski.adam.busflowpicker.services.implementations

import com.google.gson.Gson
import com.google.gson.JsonObject
import kochanski.adam.busflowpicker.model.entities.Station
import kochanski.adam.busflowpicker.model.utils.normaliseJson
import kochanski.adam.busflowpicker.model.utils.stationMappedFromJsonObject
import kochanski.adam.busflowpicker.services.Requesters.StationDataRequester
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class StationDataRequesterImpl : StationDataRequester {

    private var apiKey: String = System.getenv("api.warszawa.apikey")
    private var webClient: WebClient = WebClient.builder()
            .baseUrl("https://api.um.warszawa.pl/api/action/dbstore_get/")
            .build()

    private val resourceId: String = "ab75c33d-3a26-4342-b36a-6e5fef0a3ac3"

    private val RESOURCE_ID_NAME = "id"
    private val API_KEY_NAME = "apikey"

    override fun requestStations(): Flux<Station> {
        return webClient.get()
                .uri {
                    it.queryParam(RESOURCE_ID_NAME, resourceId)
                            .queryParam(API_KEY_NAME, apiKey)
                            .build()
                }.retrieve()
                .bodyToMono(String::class.java)
                .stationsFromJson()
    }

    private fun Mono<String>.stationsFromJson(): Flux<Station> =
            map { response ->
                Gson().fromJson(response, JsonObject::class.java)
                        .get("result").asJsonArray
                        .map { normaliseJson(it.asJsonObject) }
            }
                    .flatMapMany { Flux.fromIterable(it) }
                    .filter { it.get("szer_geo").asString != "null" && it.get("dlug_geo").asString != "null" && it.get("obowiazuje_od").asString != "null" }
                    .map { jsonElement ->
                        stationMappedFromJsonObject(jsonElement.asJsonObject)
                    }
}
