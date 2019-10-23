<template>
    <l-map :zoom="zoom" :center="center">
        <l-tile-layer :url="url" :attribution="attribution"></l-tile-layer>
        <l-marker v-for="bus in buses" :lat-lng="getLatLngFromVehicleInfo(bus)" :icon="busIcon">
            <l-tooltip :options="{permanent: true}">
                {{bus.Lines}}
            </l-tooltip>
        </l-marker>
        <l-marker v-for="tram in trams" :lat-lng="getLatLngFromVehicleInfo(tram)" :icon="tramIcon">
            {{zoom}}
            <l-tooltip :options="{permanent: true}">
                {{tram.Lines}}
            </l-tooltip>
        </l-marker>
    </l-map>
</template>

<script>
    import {LMap, LTileLayer, LMarker, LTooltip} from 'vue2-leaflet'
    import {latLng, icon} from "leaflet";

    export default {
        name: "MapPanel",
        components: {LMap, LTileLayer, LMarker, LTooltip},
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
            buses: [{Lat: 52.25265306914573, Lon: 20.898388624191284, Lines: "Test"}, {
                Lat: 52.25265306914573,
                Lon: 20.898388624191284,
                Lines: "Test"
            }, {
                Lat: 52.25265306914573,
                Lon: 20.898388624191284,
                Lines: "Test"
            }, {
                Lat: 52.25265306914573,
                Lon: 20.898388624191284,
                Lines: "Test"
            }, {
                Lat: 52.25265306914573,
                Lon: 20.898388624191284,
                Lines: "Test"
            }, {
                Lat: 52.25265306914573,
                Lon: 20.898388624191284,
                Lines: "Test"
            }],
            trams: [],
        }),
        methods: {
            getLatLngFromVehicleInfo(vehicleInfo) {
                return latLng(vehicleInfo.Lat, vehicleInfo.Lon)
            },
            getWarsawApiUrl(type, apiKey) {
                return `https://api.um.warszawa.pl/api/action/busestrams_get/?resource_id=f2e5503e-927d-4ad3-9500-4ab9e55deb59&apikey=${apiKey}&type=${type}`
            },
            updateVehicles() {
                this.$http.get(this.getWarsawApiUrl(1, process.env.VUE_APP_WARSAW_API_SECRET_KEY)).then(result => this.buses = result.data.result);
                this.$http.get(this.getWarsawApiUrl(2, process.env.VUE_APP_WARSAW_API_SECRET_KEY)).then(result => this.trams = result.data.result);
            }
        },
        mounted() {
            //this.updateVehicles();
            //window.setInterval(() => this.updateVehicles(), 10000);

        }
    }
</script>

