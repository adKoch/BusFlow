package kochanski.adam.busflowpicker.services

import javax.annotation.PostConstruct
import kochanski.adam.busflowpicker.model.DistrictGeographyRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class LoaderService(
    @Autowired val districtGeographyLoader: DistrictGeographyLoader,
    @Autowired val districtGeographyRepository: DistrictGeographyRepository
) {

    @PostConstruct
    fun doOnStart() {
        districtGeographyRepository.deleteAll()
                .log()
                .thenEmpty {
                    districtGeographyLoader.loadDistricts()
                            .map {
                                districtGeographyRepository.save(it)
                                        .log()
                                        .subscribe()
                            }
                }
                .subscribe()
    }
}
