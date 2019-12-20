<template>
    <l-map :zoom="zoom" :center="center" ref="map">
        <l-tile-layer :url="url" :attribution="attribution"/>
        <drawn-polygon v-if="drawnPolygons.length>0 && drawnPolygonsUnion!==null && showDrawTool"
                       :polygon="drawnPolygonsUnion"
                       :tooltip="customPolygonTooltip"/>
        <template v-for="district in districts">
            <district-polygon :key="district.id"
                              :district="district"/>
        </template>
        <template
                v-for="station in stations.filter(st=>!filterByPolygons || isPointInsidePolygons(st.latitude,st.longitude,filteredPointsArray))">
            <station-marker :key="station.id"
                            :station="station"
                            :circle-radius="stationRadius"
                            :max-line-count="lineCount"
                            :show-circle="showStationRadius"
                            :station-line-label="stationLineLabel"/>
        </template>
        <vehicle-clusters
                :vehicles="vehicles.filter(veh=>!filterByPolygons || isPointInsidePolygons(veh.latitude,veh.longitude,filteredPointsArray))"
                :show-tram="showTram"
                :show-bus="showBus"/>
    </l-map>
</template>

<script>
    import {LMap, LTileLayer} from 'vue2-leaflet'
    import {latLng} from "leaflet";
    import 'leaflet-draw'
    import VehicleClusters from "./map/VehicleClusters";
    import StationMarker from "./map/StationMarker";
    import DistrictPolygon from "./map/DistrictPolygon";
    import DrawnPolygon from "./map/DrawnPolygon";
    import {isPointInsidePolygons, polygonToPoints} from "../core/geometryOps";
    import {polygonUnion} from "../core/geometryOps";
    import {getDrawControl} from "../core/drawControl";

    export default {
        name: "MapPanel",
        components: {
            LMap,
            LTileLayer,
            VehicleClusters,
            DistrictPolygon,
            StationMarker,
            DrawnPolygon,
        },
        props: ['showDrawTool', 'vehicles', 'districts', 'stations', 'showBus', 'showTram', 'showStationRadius', 'stationRadius', 'lineCount', 'filterByPolygons'],
        data: () => ({
            zoom: 15.0,
            center: latLng(52.25265306914573, 20.898388624191284),
            url: 'http://{s}.tile.osm.org/{z}/{x}/{y}.png',
            attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors',
            customShapeLayer: [],
            drawControl: {},
            stationLineLabel: "Linie",
            drawnPolygons: [],
            drawnPolygon: [],
            customPolygonTooltip: "Narysowany",
            drawnFigureColor: '#ff3636',
            customShapePolygonVisibility: true,
        }),
        watch: {
            showDrawTool: {
                handler(newVal) {
                    this.toggleDrawTool(newVal);
                }
            },
            drawnPolygons: {
                handler() {
                    this.$emit('input', this.filteredPointsArray)
                }
            }
        },
        computed: {
            drawnPolygonsUnion: function () {
                return polygonUnion(this.drawnPolygons)
            },
            filteredPointsArray: function () {
                let pointsArray = [];
                if (polygonUnion(this.drawnPolygons) !== null) {
                    pointsArray.push(polygonUnion(this.drawnPolygons).geometry.coordinates[0].map(pair => {
                        return {latitude: pair[0], longitude: pair[1]}
                    }));
                }
                this.districts.forEach(district => pointsArray.push(district.coordinates.map(c => {
                    return {latitude: c.lat, longitude: c.lon}
                })));
                return pointsArray
            }
        },
        methods: {
            isPointInsidePolygons,
            getDrawControl,
            polygonToPoints,
            toggleDrawTool(isOn) {
                if (isOn) this.$refs.map.mapObject.addControl(this.drawControl);
                else this.$refs.map.mapObject.removeControl(this.drawControl);
                return isOn;
            },
            hideCustomShapeLayer() {
                this.customShapeLayer.setStyle({opacity: 0, fillOpacity: 0});
                this.customShapePolygonVisibility = true;
            },
            showCustomShapeLayer() {
                this.customShapeLayer.setStyle({opacity: 0.3, fillOpacity: 0.3});
                this.customShapePolygonVisibility = false;
            },
            registerMapEvents() {
                const map = this.$refs.map.mapObject;
                map.on(window.L.Draw.Event.CREATED, (e) => {
                    const layer = e.layer;
                    this.customShapeLayer.addLayer(layer);
                    const figure = this.polygonToPoints(layer.toGeoJSON());
                    this.drawnPolygons.push({
                        points: figure["points"]
                    });
                });
                map.on(window.L.Draw.Event.DELETED, (e) => {
                    e.layers.eachLayer(layer => {
                        let figure = this.polygonToPoints(layer.toGeoJSON());
                        figure = {
                            points: figure["points"]
                        };
                        this.drawnPolygons = this.drawnPolygons.filter(polygon => JSON.stringify(polygon) !== JSON.stringify(figure));
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
            }
        },
        mounted() {
            this.$nextTick(() => {
                const map = this.$refs.map.mapObject;
                this.customShapeLayer = new window.L.FeatureGroup().addTo(map);
                this.drawControl = this.getDrawControl(this.customShapeLayer, this.drawnFigureColor);
                this.toggleDrawTool(this.showDrawTool);
                this.registerMapEvents();
            });

        }
    }
</script>
<style scoped>
    @import '~leaflet-draw/dist/leaflet.draw-src.css';
    @import '~leaflet-draw/dist/leaflet.draw.css';
</style>
