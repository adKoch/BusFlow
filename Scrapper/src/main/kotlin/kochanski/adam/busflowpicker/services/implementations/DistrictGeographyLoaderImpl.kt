package kochanski.adam.busflowpicker.services.implementations

import com.google.gson.Gson
import com.google.gson.JsonElement
import java.io.File
import kochanski.adam.busflowpicker.model.entities.DistrictGeography
import kochanski.adam.busflowpicker.model.entities.GeoPoint
import kochanski.adam.busflowpicker.services.DistrictGeographyLoader
import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Service

@Service
class DistrictGeographyLoaderImpl : DistrictGeographyLoader {
    private final val gson = Gson()
    private final val districtsDirectory = File("warsaw_districts/")
    override fun loadDistricts(): List<DistrictGeography> {
        return FileUtils.listFiles(districtsDirectory, arrayOf("coordinates"), false).map { districtFromJsonFile(it) }
    }

    private fun districtFromJsonFile(file: File): DistrictGeography {
        val districtName = getDistrictNameFromFile(file)
        val districtCoordinates = getDistrictCoordinatesFromFile(file)
        return DistrictGeography(districtName, districtCoordinates)
    }

    private fun getDistrictNameFromFile(file: File): String {
        val fileName = file.name
        val endIndex = fileName.indexOf('.')
        return fileName.substring(0, endIndex)
    }

    private fun getDistrictCoordinatesFromFile(file: File): List<GeoPoint> =
            gson.fromJson(file.readText(), JsonElement::class.java)
                    .asJsonArray
                    .map { GeoPoint(longitude = it.asJsonArray[0].asFloat, latitude = it.asJsonArray[1].asFloat) }
}
