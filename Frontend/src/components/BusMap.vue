<template>

    <l-map :zoom="zoom" :center="center" ref="map">
        <l-tile-layer :url="url" :attribution="attribution"></l-tile-layer>
        <template v-for="station in controls.stations"
        >
            <l-marker :key="station.id"
                      :lat-lng="getLatLngFromObject(station)"
                      :icon="stationIcon">
                <l-popup>
                    <p>{{`${station.poolName} ${station.post}`}} > {{station.direction}}</p>
                    <p>{{stationLineLabel}}:
                        {{station.lines.map(line=>" "+line).toString()}}</p>
                    <p>{{geoShapes}}</p>
                </l-popup>
            </l-marker>
            <l-circle :key="station.id"
                      :lat-lng="getLatLngFromObject(station)"
                      :radius="controls.areaRadius* controls.areaMultiplier * controls.showArea"
                      :fillColor="getPartHexColor(station.lines.length, controls.areaLineMax)"
                      :fillOpacity="0.1 * controls.showArea"
                      :opacity="0.4 * controls.showArea"
                      :color="getPartHexColor(station.lines.length, controls.areaLineMax)"
            ></l-circle>
        </template>
        <l-marker-cluster
                :options="{animateAddingMarkers:false, animate:false, maxClusterRadius:60, iconCreateFunction:vehicleMarkerClusterIcon}"
                :icon="busIcon"
                class="VehicleMarkerCluster">
            <template v-for="vehicle in controls.vehicles">
                <l-marker
                        v-if="controls.showBus && vehicle.type===1 && (controls.lines.length===0 || controls.lines.includes(vehicle.line))"
                        :key="vehicle.id"
                        :lat-lng="getLatLngFromObject(vehicle)"
                        :icon="busIcon"
                        :options="{vehicleLine:vehicle.line, vehicleType: vehicle.type}">
                    <l-tooltip :options="{permanent: true}">
                        {{vehicle.line}}
                    </l-tooltip>
                </l-marker>
                <l-marker
                        v-else-if="controls.showTram && vehicle.type===2 && (controls.lines.length===0 || controls.lines.includes(vehicle.line))"
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
    import {LMap, LTileLayer, LMarker, LTooltip, LPopup, LCircle} from 'vue2-leaflet'
    import {latLng, icon, divIcon} from "leaflet";
    import Vue2LeafletMarkerCluster from 'vue2-leaflet-markercluster'
    import 'leaflet-draw'

    export default {
        name: "MapPanel",
        components: {
            LMap,
            LTileLayer,
            LMarker,
            LTooltip,
            LPopup,
            LCircle,
            'l-marker-cluster': Vue2LeafletMarkerCluster,
        },
        props: ["controls"],
        data: () => ({
            zoom: 21,
            center: latLng(52.25265306914573, 20.898388624191284),
            url: 'http://{s}.tile.osm.org/{z}/{x}/{y}.png',
            attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
            drawnPolygonColor: '#ff3636',
            drawnCircleColor: '#d1872c',
            drawnRectangleColor: '#b082ff',
            geoShapes: [],
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
            }
            ,
        },
        mounted() {
            this.$nextTick(() => {
                const map = this.$refs.map.mapObject;
                const editableLayers = new window.L.FeatureGroup().addTo(map);
                const drawControl = new window.L.Control.Draw({
                    position: 'bottomleft',
                    draw: {
                        polyline: false,
                        polygon: {
                            shapeOptions: {
                                color: this.drawnPolygonColor,
                            },
                        },
                        rectangle: {
                            shapeOptions: {
                                color: this.drawnRectangleColor,
                            }
                        },
                        circle: {
                            shapeOptions: {
                                color: this.drawnCircleColor,
                            }
                        },
                        marker: false
                    },
                    edit: {
                        featureGroup: editableLayers,
                        remove: true,
                    }
                });

                map.addControl(drawControl);


                map.on(window.L.Draw.Event.CREATED, (e) => {
                    const layer = e.layer;
                    editableLayers.addLayer(layer);
                    let newObject = {};

                    if (e.layerType === "circle") {
                        newObject = {
                            "type": "circle",
                            "center": {
                                "latitude": Object.values(layer.getLatLng())[0],
                                "longitude": Object.values(layer.getLatLng())[1],
                            },
                            "radius": layer.getRadius()
                        };
                    } else {
                        const coordinates = layer.toGeoJSON()["geometry"]["coordinates"][0].map(latLngArray => {
                            return {"latitude": latLngArray[1], "longitude": latLngArray[0]}
                        });
                        newObject = {
                            "type": "polygon",
                            "points":coordinates
                        };
                    }
                    this.geoShapes.push(newObject);
                });
            });
        }
    }
</script>
<style scoped>
    @import '~leaflet-draw/dist/leaflet.draw-src.css';
    @import '~leaflet-draw/dist/leaflet.draw.css';
</style>
