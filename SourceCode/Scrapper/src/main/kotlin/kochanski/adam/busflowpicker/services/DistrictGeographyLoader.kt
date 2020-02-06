package kochanski.adam.busflowpicker.services

import kochanski.adam.busflowpicker.model.entities.DistrictGeography

interface DistrictGeographyLoader {
    fun loadDistricts(): List<DistrictGeography>
}
