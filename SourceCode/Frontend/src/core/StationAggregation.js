import {isPointInsidePolygons} from "./geometryOps";


function getStationCountByLines(pointsArray, stations) {
    let countBylines = {};
    stations.filter(station =>
        isPointInsidePolygons(station.latitude, station.longitude, pointsArray))
        .forEach(station => {
            station.lines.forEach(line => {
                if (line in countBylines) countBylines[line]++;
                else countBylines[line] = 1;
            })
        });
    return countBylines
}


export function stationCountByLines(pointsArray, stations) {
    return getStationCountByLines(pointsArray, stations)
}