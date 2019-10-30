<template>
    <l-map :zoom="zoom" :center="center">
        <l-icon-default :image-path="'/statics/leafletImages/'"></l-icon-default>
        <l-tile-layer :url="url" :attribution="attribution"></l-tile-layer>
        <l-marker-cluster :options="{animateAddingMarkers:false, animate:false, maxClusterRadius:60, iconCreateFunction:vehicleMarkerClusterIcon}" :icon="busIcon">
            <template v-for="vehicle in vehicles">
                <l-marker v-if="vehicle.type===1"
                          :key="vehicle.id"
                          :lat-lng="getLatLngFromVehicleInfo(vehicle)"
                          :icon="busIcon"
                          :options="{vehicleLine:vehicle.line, vehicleType: vehicle.type}">
                    <l-tooltip :options="{permanent: true}">
                        {{vehicle.line}}
                    </l-tooltip>
                    <l-popup>
                        {{vehicle}}
                    </l-popup>
                </l-marker>
                <l-marker v-else-if="vehicle.type===2"
                          :key="vehicle.id"
                          :lat-lng="getLatLngFromVehicleInfo(vehicle)"
                          :icon="tramIcon"
                          :options="{vehicleLine:vehicle.line, vehicleType: vehicle.type}">
                    <l-tooltip :options="{permanent: true}" :content="vehicle.line">
                    </l-tooltip>
                    <l-popup>
                        {{vehicle}}
                    </l-popup>
                </l-marker>
            </template>

        </l-marker-cluster>
    </l-map>
</template>

<script>
    import {LMap, LTileLayer, LMarker, LTooltip, LPopup} from 'vue2-leaflet'
    import {latLng, icon, divIcon} from "leaflet";
    import Vue2LeafletMarkerCluster from 'vue2-leaflet-markercluster'

    export default {
        name: "MapPanel",
        components: {LMap, LTileLayer, LMarker, LTooltip, LPopup, 'l-marker-cluster': Vue2LeafletMarkerCluster},
        props: ["controls"],
        data: () => ({
            zoom: 21,
            center: latLng(52.25265306914573, 20.898388624191284),
            url: 'http://{s}.tile.osm.org/{z}/{x}/{y}.png',
            apiUrl: "http://localhost:8282",
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
            vehicles: [],
            busesName:"Autobusy",
            tramName:"Tramwaje"
        }),
        methods: {
            getLatLngFromVehicleInfo(vehicleInfo) {
                return latLng(vehicleInfo.latitude, vehicleInfo.longitude)
            },
            getApiUrl(lines) {
                return `${this.apiUrl}/vehicle_location?lines=`.concat(lines);
            },
            updateVehicles() {
                this.$http.get(this.getApiUrl(this.controls.lines)).then(result => this.vehicles = result.data);
            },
            summedClusterTooltip: function (childMarkers) {
                return`${this.busesName}
                    ${childMarkers.filter(marker=>marker.options.vehicleType===1)
                        .map(marker=>`${marker.options.vehicleLine}\n`)}
                        ${this.tramName}
                    ${childMarkers.filter(marker=>marker.options.vehicleType===2)
                        .map(marker=>`\n ${marker.options.vehicleLine}`)}
                `
            },
            //Default functionality
            vehicleMarkerClusterIcon: function (cluster) {
               // console.log(cluster.getAllChildMarkers());
               // console.log(this.summedClusterTooltip(cluster.getAllChildMarkers()));

                return divIcon({
                    className: "VehicleMarkerCluster",
                    html: `
                    <img src=${require('../assets/VehicleClusterIcon.png')} height="25px" width="25px" class="VehicleMarkerCluster">
                    `,
                    iconSize: this.vehicleMarkerClusterIconSpecification.iconSize,
                    iconAnchor: this.vehicleMarkerClusterIconSpecification.iconAnchor,
                })
            },
        },
        mounted() {
            this.updateVehicles();
            window.setInterval(() => this.updateVehicles(), 10000);

        }
    }
</script>
<style scoped>
    @import "~leaflet.markercluster/dist/MarkerCluster.css";
    @import "~leaflet.markercluster/dist/MarkerCluster.Default.css";

    .VehicleMarkerCluster{
        width: 25px;
        height: 25px;
    }
</style>

