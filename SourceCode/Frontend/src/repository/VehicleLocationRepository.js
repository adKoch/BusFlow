import axios from "axios"

const apiUrl = "http://localhost:8282";
const route = "/vehicle_location";

function getUrlWithLines(lines) {
    return apiUrl + route + `?lines=${lines}`
}

function requestVehicleLocationDataByLines(lines) {
    return axios.get(getUrlWithLines(lines))
}

export default {
    findAllByLines(lines) {
        return requestVehicleLocationDataByLines(lines)
    }
}