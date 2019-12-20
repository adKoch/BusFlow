<template>
    <div>
        <l-marker :key="station.id"
                  :lat-lng="getLatLngFromObject(station)"
                  :icon="stationIcon">
            <l-popup>
                <p>{{`${station.poolName} ${station.post}`}} > {{station.direction}}</p>
                <p>{{stationLineLabel}}:
                    {{station.lines.map(line=>" "+line).toString()}}</p>
            </l-popup>
        </l-marker>
        <template v-if="showCircle">
            <l-circle
                    :lat-lng="getLatLngFromObject(station)"
                    :radius="circleRadius"
                    :fillColor="partAsHexColor(station.lines.length, maxLineCount)"
                    :fillOpacity="0.1"
                    :opacity="0.4"
                    :color="partAsHexColor(station.lines.length, maxLineCount)"
            />
        </template>
    </div>
</template>

<script>
    import {icon, latLng} from "leaflet";
    import {partAsHexColor} from "../../../core/colorOps";
    import {LMarker, LPopup, LCircle} from 'vue2-leaflet'

    export default {
        name: "StationMarker",
        props: ['station', 'circleRadius', 'showCircle', 'maxLineCount', 'stationLineLabel'],
        components: {
            LMarker, LPopup, LCircle
        },
        data: () => ({
            stationIcon: icon({
                iconUrl: require("../../../assets/StationIcon.png"),
                iconSize: [20, 20],
                iconAnchor: [10, 10]
            }),
        }),
        methods: {
            getLatLngFromObject(objectWithLatLng) {
                return latLng(objectWithLatLng.latitude, objectWithLatLng.longitude)
            },
            partAsHexColor
        },
    }
</script>

<style scoped>

</style>