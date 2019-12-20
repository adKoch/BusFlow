<template>
    <l-polygon
            :lat-lngs="district.coordinates.map(coordinates=>
                   getLatLngFromLatLon(coordinates.lat,coordinates.lon))"
            :color="drawnDistrictColor"
            :fillColor="drawnDistrictColor">
        <l-tooltip>{{district.districtName}}</l-tooltip>
    </l-polygon>
</template>

<script>
    import { LTooltip, LPolygon} from 'vue2-leaflet'
    import {latLng} from "leaflet";

    export default {
        name: "DistrictPolygon",
        props:['district'],
        components:{
            LPolygon,
            LTooltip
        },
        data:()=>({
            drawnDistrictColor: '#ee2020'
        }),
        methods:{
            getLatLngFromLatLon(latitude, longitude) {
                return latLng(latitude, longitude)
            },
        }
        ,
    }
</script>