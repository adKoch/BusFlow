<template>
    <l-polygon :lat-lngs="polygon.geometry.coordinates[0]"
               :color="drawnFigureColor"
               :fillColor="drawnFigureColor">
        <l-tooltip>{{tooltip}}</l-tooltip>
    </l-polygon>
</template>

<script>
    import {LTooltip, LPolygon} from 'vue2-leaflet'

    export default {
        name: "DrawnPolygon",
        props: ['polygon', 'tooltip'],
        components: {
            LPolygon,
            LTooltip
        },
        data: () => ({
            drawnFigureColor: '#ff3636',
        })
    }
</script>

<style scoped>

</style>