import {polygon, union} from "@turf/turf";

function polygonsToTurfPolygons(polygons) {
    let retPolygons = [];
    polygons.forEach(pol => {
        retPolygons.push(polygon([pol.points.map(point => {
            return [point.latitude, point.longitude]
        })]));
    });
    return retPolygons;
}

function pointInsidePolygon(latitude, longitude, points) {
    let i = 0;
    let j = points.length - 1;
    let result = false;
    while (i < points.length) {
        if (points[i].latitude > latitude !== points[j].latitude > latitude &&
            longitude < (points[j].longitude - points[i].longitude) *
            (latitude - points[i].latitude) / (points[j].latitude - points[i].latitude) + points[i].longitude) {
            result = !result
        }
        j = i++
    }
    return result;
}

function pointInsidePolygons(latitude, longitude, pointsArray) {
    if (pointsArray.length === 0) return true;
    let isInside = false;
    pointsArray.forEach(polygon => {
            if (!isInside && isPointInsidePolygon(latitude, longitude, polygon)) {
                isInside = true
            }
        }
    );
    return isInside
}

function getAsTurfUnion(polygons) {
    let figure = null;
    polygonsToTurfPolygons(polygons).forEach(geoJson => {
        if (figure === null) {
            figure = geoJson;
        } else {
            figure = union(figure, geoJson);
        }
    });
    return figure;
}
function getPolygonToPoints(polygonGeoJson) {
    let newObject;
    const coordinates = polygonGeoJson["geometry"]["coordinates"][0].map(latLngArray => {
        return {"latitude": latLngArray[1], "longitude": latLngArray[0]}
    });
    newObject = {
        "type": "polygon",
        "points": coordinates
    };
    return newObject;
}

export function polygonToPoints(polygonGeoJson) {
    return getPolygonToPoints(polygonGeoJson)
}

export function polygonUnion(polygons) {
    return getAsTurfUnion(polygons)
}

export function isPointInsidePolygon(latitude, longitude, points) {
    return pointInsidePolygon(latitude, longitude, points)
}

export function isPointInsidePolygons(latitude, longitude, pointsArray) {
    return pointInsidePolygons(latitude, longitude, pointsArray)
}