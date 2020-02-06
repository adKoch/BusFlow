package kochanski.adam.busflowpicker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class BusflowpickerApplication

fun main(args: Array<String>) {
    runApplication<BusflowpickerApplication>(*args)
}
