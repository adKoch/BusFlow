package kochanski.adam.busflowpicker.services.implementations

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.time.LocalDateTime
import kochanski.adam.busflowpicker.model.VehicleLocation
import kochanski.adam.busflowpicker.model.VehicleLocation.VehicleCreator.vehicleMappedFromJsonObject
import kochanski.adam.busflowpicker.services.MessageService
import kochanski.adam.busflowpicker.services.RequesterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Flux.fromIterable
import reactor.core.publisher.Mono

@Component
class VehicleRequesterService : RequesterService {

    @Autowired
    private lateinit var messageService: MessageService
    private val MESSAGE_TITLE = "VehicleRequester"

    private var apiKey: String = System.getenv("api.warszawa.apikey")
    private var webClient: WebClient = WebClient.create("https://api.um.warszawa.pl/api/action/busestrams_get/")

    @Value("\${api.warszawa.resourceId}")
    private lateinit var resourceId: String
    @Value("\${api.warszawa.types}")
    private lateinit var vehicleTypes: List<Int>

    private val RESOURCE_ID_NAME = "resource_id"
    private val API_KEY_NAME = "apikey"
    private val TYPE_NAME = "type"

    override fun requestVehicles(): Flux<VehicleLocation> {
        val timeRequested = LocalDateTime.now()
        return vehicleTypes.map { vehicleType ->
            requestVehicleData(vehicleType, timeRequested)
        }
                .reduce { requestedVehicles1, requestedVehicles2 ->
                    Flux.concat(requestedVehicles1, requestedVehicles2)
                }
    }

    private fun requestVehicleData(vehicleType: Int, timeRequested: LocalDateTime): Flux<VehicleLocation> {
        return webClient.get()
                .uri { uriBuilder ->
                    uriBuilder.queryParam(RESOURCE_ID_NAME, resourceId)
                            .queryParam(API_KEY_NAME, apiKey)
                            .queryParam(TYPE_NAME, vehicleType)
                            .build()
                }
                .retrieve()
                .bodyToMono(String::class.java)
                .vehicleMapFromJson(vehicleType, timeRequested)
                .onErrorMap { requestError ->
                    messageService.sendError(title = MESSAGE_TITLE,
                            error = requestError.toString(),
                            message = "Error encountered during requesting vehicle data of type $vehicleType")
                    throw requestError
                }
    }

    private fun Mono<String>.vehicleMapFromJson(ofType: Int, ofTime: LocalDateTime): Flux<VehicleLocation> =
            map { response ->
                Gson().fromJson(response, JsonObject::class.java).get("result").asJsonArray
            }
                    .flatMapMany { fromIterable(it) }
                    .map { jsonElement ->
                        vehicleMappedFromJsonObject(jsonElement.asJsonObject, ofType, ofTime)
                    }
}
