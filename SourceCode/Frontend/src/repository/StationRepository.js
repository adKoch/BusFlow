import axios from "axios"

const apiUrl = "http://localhost:8282";
const route = "/station";

function getUrlWithLines(lines){
    return apiUrl+route+`?lines=${lines}`
}

function requestStationDataByLines(lines) {
    return axios.get(getUrlWithLines(lines))
}

export default {
    findAllByLines(lines) {
        return requestStationDataByLines(lines)
    }
}
