<template>
    <l-map :zoom="zoom" :center="center">
        <l-icon-default :image-path="'/statics/leafletImages/'"></l-icon-default>
        <l-tile-layer :url="url" :attribution="attribution"></l-tile-layer>
        <l-marker-cluster :options="{animateAddingMarkers:false, animate:false, maxClusterRadius:60}" @spiderfied="1">
            <template v-for="vehicle in vehicles">
                <l-marker v-if="vehicle.type===1" v-bind:key="vehicle.id" :lat-lng="getLatLngFromVehicleInfo(vehicle)"
                          v-bind:icon="busIcon">
                    <l-tooltip :options="{permanent: true}">
                        {{vehicle.line}}
                    </l-tooltip>
                    <l-popup>
                        {{vehicle}}
                    </l-popup>
                </l-marker>
                <l-marker v-else-if="vehicle.type===2" v-bind:key="vehicle.id"
                          :lat-lng="getLatLngFromVehicleInfo(vehicle)"
                          v-bind:icon="tramIcon">
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
    import {latLng, icon} from "leaflet";
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
            vehicles: [{
                latitude: 52.25265306914573,
                longitude: 20.898388624191284,
                line: `localhost:8282/vehicle_location?lines=`.concat(["122", "154", "133", "155"])
            }],
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
            }
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
</style>

