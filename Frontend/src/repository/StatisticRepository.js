import axios from "axios"

const apiUrl = "http://localhost:8282";
const baseRoute = "/vehicle_location";
const lineCountRoute = "/line_count_statistics";
const vehicleCountRoute = "/count_statistics";

function getUrlWithParams(additionalRoute, polygonLatitudes, polygonLongitudes, start, end, interval, lines) {
    return apiUrl + baseRoute + additionalRoute +
    `?polygonLatitudes=${polygonLatitudes}` +
    `&polygonLongitudes=${polygonLongitudes}` +
    `&start=${start}` +
    `&end=${end}` +
    `&interval=${interval}` +
    lines.length >= 1 ? `&lines=${lines}` : ""
}

function requestVehicleLocationCount(polygonLatitudes, polygonLongitudes, start, end, interval, lines) {
    return axios.get(getUrlWithParams(vehicleCountRoute, polygonLatitudes, polygonLongitudes, start, end, interval, lines))
}

function requestVehicleLineCount(polygonLatitudes, polygonLongitudes, start, end, interval, lines) {
    return axios.get(getUrlWithParams(lineCountRoute, polygonLatitudes, polygonLongitudes, start, end, interval, lines))
}

export default {
    findVehicleCountStatistic(polygonLatitudes, polygonLongitudes, start, end, interval, lines) {
        return requestVehicleLocationCount(polygonLatitudes, polygonLongitudes, start, end, interval, lines)
    },

    findVehicleLineCountStatistic(polygonLatitudes, polygonLongitudes, start, end, interval, lines) {
        return requestVehicleLineCount(polygonLatitudes, polygonLongitudes, start, end, interval, lines)
    }
}