package kochanski.adam.busflowpicker.services.implementations

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.time.LocalDateTime
import kochanski.adam.busflowpicker.model.entities.VehicleLocation
import kochanski.adam.busflowpicker.model.utils.vehicleLocationMappedFromJsonObject
import kochanski.adam.busflowpicker.services.Requesters.VehicleDataRequester
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono

@Component
class VehicleDataRequesterImpl : VehicleDataRequester {

    private var apiKey: String = System.getenv("api.warszawa.apikey")
    private var webClient: WebClient = WebClient.builder()
            .baseUrl("https://api.um.warszawa.pl/api/action/busestrams_get/")
            .build()

    @Value("\${api.warszawa.resourceId}")
    private lateinit var resourceId: String
    @Value("\${api.warszawa.types}")
    private lateinit var vehicleTypes: List<Int>

    private val RESOURCE_ID_NAME = "resource_id"
    private val API_KEY_NAME = "apikey"
    private val TYPE_NAME = "type"
    private val retryCount = 2L

    override fun requestVehicles(): Flux<VehicleLocation> {
        return vehicleTypes
                .map { vehicleType ->
                    requestVehicleData(vehicleType, retryCount)
                }
                .reduce { requestedVehicles1, requestedVehicles2 ->
                    Flux.concat(requestedVehicles1, requestedVehicles2)
                }
    }

    private fun requestVehicleData(vehicleType: Int, retryCount: Long): Flux<VehicleLocation> {
        return webClient.get()
                .uri { uriBuilder ->
                    uriBuilder.queryParam(RESOURCE_ID_NAME, resourceId)
                            .queryParam(API_KEY_NAME, apiKey)
                            .queryParam(TYPE_NAME, vehicleType)
                            .build()
                }
                .retrieve()
                .bodyToMono(String::class.java)
                .vehicleMapFromJson(vehicleType)
                .retry(retryCount)
    }

    private fun Mono<String>.vehicleMapFromJson(ofType: Int): Flux<VehicleLocation> =
            map { response ->
                Gson().fromJson(response, JsonObject::class.java).get("result").asJsonArray
            }
                    .flatMapMany { fromIterable(it) }
                    .map { jsonElement ->
                        vehicleLocationMappedFromJsonObject(jsonElement.asJsonObject, ofType, LocalDateTime.now())
                    }
}
