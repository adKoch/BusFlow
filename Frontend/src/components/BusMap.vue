<template>
    <l-map :zoom="zoom" :center="center">
        <l-tile-layer :url="url" :attribution="attribution"></l-tile-layer>
        <l-marker-cluster
                :options="{animateAddingMarkers:false, animate:false, maxClusterRadius:60, iconCreateFunction:vehicleMarkerClusterIcon}"
                :icon="busIcon"
                class="VehicleMarkerCluster">
            <template v-for="vehicle in controls.vehicles">
                <l-marker v-if="controls.showBus && vehicle.type===1"
                          :key="vehicle.id"
                          :lat-lng="getLatLngFromVehicleInfo(vehicle)"
                          :icon="busIcon"
                          :options="{vehicleLine:vehicle.line, vehicleType: vehicle.type}">
                    <l-tooltip :options="{permanent: true}">
                        {{vehicle.line}}
                    </l-tooltip>
                </l-marker>
                <l-marker v-else-if="controls.showTram && vehicle.type===2"
                          :key="vehicle.id"
                          :lat-lng="getLatLngFromVehicleInfo(vehicle)"
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
    import {LMap, LTileLayer, LMarker, LTooltip} from 'vue2-leaflet'
    import {latLng, icon, divIcon} from "leaflet";
    import Vue2LeafletMarkerCluster from 'vue2-leaflet-markercluster'

    export default {
        name: "MapPanel",
        components: {LMap, LTileLayer, LMarker, LTooltip, 'l-marker-cluster': Vue2LeafletMarkerCluster},
        props: ["controls"],
        data: () => ({
            zoom: 21,
            center: latLng(52.25265306914573, 20.898388624191284),
            url: 'http://{s}.tile.osm.org/{z}/{x}/{y}.png',
            attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
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
            vehicleMarkerClusterIconSpecification: {
                iconUrl: require("../assets/VehicleClusterIcon.png"),
                iconSize: [24, 24],
                iconAnchor: [12, 12]
            },
            maxLinesPerCluster: 3
        }),
        methods: {
            getLatLngFromVehicleInfo(vehicleInfo) {
                return latLng(vehicleInfo.latitude, vehicleInfo.longitude)
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
        }
    }
</script>