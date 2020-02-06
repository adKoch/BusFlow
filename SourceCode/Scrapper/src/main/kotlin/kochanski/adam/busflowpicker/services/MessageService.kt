package kochanski.adam.busflowpicker.services

interface MessageService {

    fun sendInfo(title: String, message: String)

    fun sendError(title: String, message: String, error: String)
}
