<template>

    <l-map :zoom="zoom" :center="center" ref="map">
        <l-tile-layer :url="url" :attribution="attribution"></l-tile-layer>
        <l-polygon v-if="controls.polygons.length>0"
                   :lat-lngs="getAsTurfUnion(controls.polygons).geometry.coordinates[0]"
                   :color="drawnFigureColor"
                   :fillColor="drawnFigureColor"/>
        <template v-for="station in controls.stations">
            <l-marker :key="station.id"
                      :lat-lng="getLatLngFromObject(station)"
                      :icon="stationIcon"
                      v-if="isPointInsidePolygons(station.latitude,station.longitude,controls.polygons)">
                <l-popup>
                    <p>{{`${station.poolName} ${station.post}`}} > {{station.direction}}</p>
                    <p>{{stationLineLabel}}:
                        {{station.lines.map(line=>" "+line).toString()}}</p>
                    <p>{{getAsTurfUnion(controls.polygons)===null?"":getAsTurfUnion(controls.polygons)}}</p>
                </l-popup>
            </l-marker>
            <l-circle :key="station.id"
                      :lat-lng="getLatLngFromObject(station)"
                      :radius="controls.areaRadius* controls.areaMultiplier * controls.showArea"
                      :fillColor="getPartHexColor(station.lines.length, controls.areaLineMax)"
                      :fillOpacity="0.1 * controls.showArea"
                      :opacity="0.4 * controls.showArea"
                      :color="getPartHexColor(station.lines.length, controls.areaLineMax)"
                      v-if="isPointInsidePolygons(station.latitude,station.longitude,controls.polygons)">
                >
            </l-circle>
        </template>
        <l-marker-cluster
                :options="{animateAddingMarkers:false, animate:false, maxClusterRadius:60, iconCreateFunction:vehicleMarkerClusterIcon}"
                :icon="busIcon"
                class="VehicleMarkerCluster">
            <template v-for="vehicle in controls.vehicles">
                <l-marker
                        v-if="controls.showBus &&
                        vehicle.type===1 &&
                        (controls.lines.length===0 || controls.lines.includes(vehicle.line)) &&
                        isPointInsidePolygons(vehicle.latitude,vehicle.longitude,controls.polygons)"
                        :key="vehicle.id"
                        :lat-lng="getLatLngFromObject(vehicle)"
                        :icon="busIcon"
                        :options="{vehicleLine:vehicle.line, vehicleType: vehicle.type}">
                    <l-tooltip :options="{permanent: true}">
                        {{vehicle.line}}
                    </l-tooltip>
                </l-marker>
                <l-marker
                        v-else-if="controls.showTram &&
                        vehicle.type===2 &&
                        controls.lines.includes(vehicle.line) &&
                        isPointInsidePolygons(vehicle.latitude,vehicle.longitude,controls.polygons)"
                        :key="vehicle.id"
                        :lat-lng="getLatLngFromObject(vehicle)"
                        :icon="tramIcon"
                        :options="{vehicleLine:vehicle.line, vehicleType: vehicle.type}">
                    <l-tooltip :options="{permanent: true}" :content="vehicle.line">
                    </l-tooltip>
                </l-marker>
            </template>
        </l-marker-cluster>
    </l-map>
</template>

<script>
    import {LMap, LTileLayer, LMarker, LTooltip, LPopup, LCircle, LPolygon} from 'vue2-leaflet'
    import {latLng, icon, divIcon} from "leaflet";
    import Vue2LeafletMarkerCluster from 'vue2-leaflet-markercluster'
    import 'leaflet-draw'
    import {polygon, union} from '@turf/turf'

    export default {
        name: "MapPanel",
        components: {
            LMap,
            LTileLayer,
            LMarker,
            LTooltip,
            LPopup,
            LCircle,
            LPolygon,
            'l-marker-cluster': Vue2LeafletMarkerCluster,
        },
        props: ["controls"],
        data: () => ({
            zoom: 21,
            center: latLng(52.25265306914573, 20.898388624191284),
            url: 'http://{s}.tile.osm.org/{z}/{x}/{y}.png',
            attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
            drawnFigureColor: '#ff3636',
            customShapeLayer: [],
            customShapePolygon: {},
            busIcon: icon({
                iconUrl: require("../assets/BusIcon.png"),
                iconSize: [20, 20],
                iconAnchor: [10, 10]
            }),
            tramIcon: icon({
                iconUrl: require("../assets/TramIcon.png"),
                iconSize: [20, 20],
                iconAnchor: [10, 10]
            }),
            stationIcon: icon({
                iconUrl: require("../assets/StationIcon.png"),
                iconSize: [20, 20],
                iconAnchor: [10, 10]
            }),
            vehicleMarkerClusterIconSpecification: {
                iconUrl: require("../assets/VehicleClusterIcon.png"),
                iconSize: [24, 24],
                iconAnchor: [12, 12]
            },
            maxLinesPerCluster: 3,
            stationLineLabel: "Linie",
            customShapePolygonVisibility: true,
        }),
        methods: {
            getPartHexColor(value, maxValue) {
                const lightness = 1 - value / maxValue < 0.2 ? 0.2 : 1 - value / maxValue;
                const saturation = value / maxValue < 0.2 ? 0.2 : value / maxValue;
                const h = 6 * value / maxValue;
                const c = (1 - Math.abs(2 * lightness - 1)) * saturation;
                const x = c * (1 - Math.abs(h % 2 - 1));
                const m = lightness - c / 2;
                if (h <= 1) return `#${Math.trunc((c + m) * 255).toString(16)}${Math.trunc((x + m) * 255).toString(16)}00`;
                else if (h <= 2) return `#${Math.trunc((x + m) * 255).toString(16)}${Math.trunc((c + m) * 255).toString(16)}00`;
                else if (h <= 3) return `#00${Math.trunc((c + m) * 255).toString(16)}${Math.trunc((x + m) * 255).toString(16)}`;
                else if (h <= 4) return `#00${Math.trunc((x + m) * 255).toString(16)}${Math.trunc((c + m) * 255).toString(16)}`;
                else if (h <= 5) return `#${Math.trunc((x + m) * 255).toString(16)}00${Math.trunc((c + m) * 255).toString(16)}`;
                else return `#${Math.trunc((c + m) * 255).toString(16)}00${Math.trunc((x + m) * 255).toString(16)}`;

            },
            getLatLngFromObject(objectWithLatLng) {
                return latLng(objectWithLatLng.latitude, objectWithLatLng.longitude)
            },
            countedLinesFromChildren: function (childMarkers) {
                return childMarkers
                    .map(marker => {
                        return {line: marker.options.vehicleLine, count: 1};
                    })
                    .reduce((a, b) => {
                        a[b.line] = (a[b.line] || 0) + b.count;
                        return a;
                    }, {});
            },
            formatLines: function (countedLines) {
                let formattedLines = "";
                let iter = 0;
                for (let countedLineKey of Object.keys(countedLines)) {
                    if (iter === this.maxLinesPerCluster) {
                        formattedLines += "<div>...<div/>";
                        break;
                    } else {
                        iter++;
                    }
                    if (countedLines[countedLineKey] === 1) {
                        formattedLines += `<div>${countedLineKey}\n<div/>`
                    } else {
                        formattedLines += `<div>${countedLineKey} x${countedLines[countedLineKey]}\n<div/>`;
                    }
                }
                return formattedLines;
            },
            vehicleMarkerClusterIcon: function (cluster) {
                return divIcon({
                    className: "VehicleMarkerCluster",
                    html: `
                    <img src=${require('../assets/VehicleClusterIcon.png')} height="25px" width="25px"/>
                    <span style="visibility: visible;
                            min-width: 50px;
                            background-color: white;
                            color: #000;
                            text-align: left;
                            padding: 5px;
                            border-radius: 6px;
                            position: absolute;
                            z-index: 1;
                            top: -20px;
                            left: 105%; ">
                            ${this.formatLines(this.countedLinesFromChildren(cluster.getAllChildMarkers()))}
                        </span>
                    `,
                    iconSize: this.vehicleMarkerClusterIconSpecification.iconSize,
                    iconAnchor: this.vehicleMarkerClusterIconSpecification.iconAnchor,
                })
            },
            normaliseDrawnShapeLayer(shapeLayer) {
                let newObject = {};
                let type = "polygon";
                try {
                    shapeLayer.getLatLng();
                    type = "circle"
                } catch (e) {
                    type = "polygon"
                }
                if (type === "circle") {
                    newObject = {
                        "type": "circle",
                        "center": {
                            "latitude": Object.values(shapeLayer.getLatLng())[0],
                            "longitude": Object.values(shapeLayer.getLatLng())[1],
                        },
                        "radius": shapeLayer.getRadius()
                    };
                } else {
                    const coordinates = shapeLayer.toGeoJSON()["geometry"]["coordinates"][0].map(latLngArray => {
                        return {"latitude": latLngArray[1], "longitude": latLngArray[0]}
                    });
                    newObject = {
                        "type": "polygon",
                        "points": coordinates
                    };
                }
                return newObject;
            },
            isPointInsidePolygon(latitude, longitude, polygon) {
                const points = polygon.points;
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
            },
            isPointInsidePolygons(latitude, longitude, polygons) {
                if (polygons.length === 0) return true;
                let isInside = false;
                polygons.forEach(polygon => {
                        if (!isInside && this.isPointInsidePolygon(latitude, longitude, polygon)) {
                            isInside = true
                        }
                    }
                );
                return isInside
            },
            hideCustomShapeLayer() {
                this.customShapeLayer.setStyle({opacity: 0, fillOpacity: 0});
                this.customShapePolygonVisibility = true;
            },
            showCustomShapeLayer() {
                this.customShapeLayer.setStyle({opacity: 0.3, fillOpacity: 0.3});
                this.customShapePolygonVisibility = false;
            },
            polygonsToTurfPolygons(polygons) {
                let retPolygons = [];
                polygons.forEach(pol => {
                    retPolygons.push(polygon([pol.points.map(point => {
                        return [point.latitude, point.longitude]
                    })]));
                });
                return retPolygons;
            },
            getAsTurfPolygons(polygons) {
                let figures = [];
                this.polygonsToTurfPolygons(polygons).forEach(polygon => figures.push(polygon));
                return figures;
            },
            getAsTurfUnion(polygons) {
                let figure = null;
                this.getAsTurfPolygons(polygons).forEach(geoJson => {
                    if (figure === null) {
                        figure = geoJson;
                    } else {
                        figure = union(figure, geoJson);
                    }
                });
                return figure;
            },
        },
        mounted() {
            this.$nextTick(() => {
                const map = this.$refs.map.mapObject;
                this.customShapeLayer = new window.L.FeatureGroup().addTo(map);
                const drawControl = new window.L.Control.Draw({
                    position: 'bottomleft',
                    draw: {
                        polyline: false,
                        polygon: {
                            shapeOptions: {
                                color: this.drawnFigureColor,
                            },
                        },
                        rectangle: {
                            shapeOptions: {
                                color: this.drawnFigureColor,
                            }
                        },
                        circle: false,
                        circlemarker: false,
                        marker: false
                    },
                    edit: {
                        featureGroup: this.customShapeLayer,
                        remove: true,
                        edit: false,
                    }
                });
                map.addControl(drawControl);
                map.on(window.L.Draw.Event.CREATED, (e) => {
                    const layer = e.layer;
                    this.customShapeLayer.addLayer(layer);
                    const figure = this.normaliseDrawnShapeLayer(layer);
                    this.controls.polygons.push({
                        points: figure["points"]
                    })

                });
                map.on(window.L.Draw.Event.DELETED, (e) => {
                    e.layers.eachLayer(layer => {
                        let figure = this.normaliseDrawnShapeLayer(layer);
                        figure = {
                            points: figure["points"]
                        };
                        this.controls.polygons = this.controls.polygons.filter(polygon => JSON.stringify(polygon) !== JSON.stringify(figure));
                    });
                });
                map.on(window.L.Draw.Event.DRAWSTART, () => {
                    this.showCustomShapeLayer()
                });
                map.on(window.L.Draw.Event.DELETESTART, () => {
                    this.showCustomShapeLayer()
                });
                map.on(window.L.Draw.Event.DRAWSTOP, () => {
                    this.hideCustomShapeLayer()
                });
                map.on(window.L.Draw.Event.DELETESTOP, () => {
                    this.hideCustomShapeLayer()
                });
            });
        }
    }
</script>
<style scoped>
    @import '~leaflet-draw/dist/leaflet.draw-src.css';
    @import '~leaflet-draw/dist/leaflet.draw.css';
</style>
