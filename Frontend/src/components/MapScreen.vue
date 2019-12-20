<template>
    <v-app>
        <v-navigation-drawer v-model="statisticsPanelVisibility" right app fixed clipped stateless width="420px">
            <statistics-panel :shown-lines="lines" :shown-stations="stations" :shown-polygons-points="drawnPoints"/>
        </v-navigation-drawer>
        <v-navigation-drawer v-model="controlPanelVisibility" app fixed clipped stateless>
            <div class="controlPanel">
                <v-alert :type="requestAlert.type"
                         class="mt-2"
                         dismissible
                         v-show="requestAlert.visible"
                         dense
                         prominent
                         outlined>
                    {{requestAlert.message}}
                </v-alert>
                <v-card v-if="lines.length!==0"
                        ref="requestStatusCard"
                        loader-height="7"
                        :loading="dataLoad.isBeingLoaded"
                        class="mt-4">
                    <v-container>
                        <v-row align="center">
                            <v-col>
                                <v-card-text>
                                    {{dataLoadingText}} {{dataLoad.interval-dataLoad.state}}s
                                </v-card-text>
                            </v-col>
                            <v-col>
                                <v-progress-circular :value="dataLoadingBarValue"
                                                     size="60"
                                                     width="7"
                                                     color="indigo"
                                                     class="text-center"
                                                     rotate="270"/>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card>
                <v-tabs centered color="indigo" class="mt-4">
                    <v-tab>{{exploreLabel}}</v-tab>
                    <v-tab>{{studyLabel}}</v-tab>
                    <v-tab-item>
                        <v-row>
                            <v-checkbox v-model="showBus"
                                        :label="showBusLabel"
                                        class="mx-2"/>
                            <v-checkbox v-model="showTram"
                                        :label="showTramLabel"
                                        class="mx-2"/>
                        </v-row>
                        <v-combobox v-model="lines"
                                    chips
                                    multiple
                                    :label="linesLabel"
                                    :hint="linesHint"
                                    v-on:change="dataLoad.linesChanged=true">
                        </v-combobox>
                        <v-row>
                            <v-checkbox v-model="showDrawTool"
                                        :label="showDrawToolLabel"
                                        class="mx-2"/>
                        </v-row>
                        <v-row>
                            <v-select class="mx-5" :items="districtsAvailable"
                                      v-model="districtsShown"
                                      :item-text="(district)=>district.districtName"
                                      multiple
                                      :label="districtChoiceLabel"/>
                        </v-row>
                    </v-tab-item>
                    <v-tab-item>
                        <v-row>
                            <v-checkbox v-model="showArea"
                                        :label="showAreaLabel"
                                        class="mx-2"/>
                        </v-row>
                        <v-row>
                            <v-form>
                                <v-text-field
                                        v-model="areaLineMax"
                                        type="number"
                                        class="mx-5"
                                        :label="areaLineMaxLabel"
                                        :disabled="!showArea"
                                />
                                <v-text-field
                                        v-model="areaRadius"
                                        type="number"
                                        class="mx-5"
                                        :label="areaRadiusLabel"
                                        :disabled="!showArea"
                                />
                            </v-form>
                            <v-btn-toggle v-model="areaMultiplier" tile group color="indigo" class="ma-5">
                                <v-btn :value=1 :disabled="!showArea">
                                    <span>m</span>
                                </v-btn>
                                <v-btn :value=1000 :disabled="!showArea">
                                    <span>km</span>
                                </v-btn>
                            </v-btn-toggle>
                        </v-row>
                    </v-tab-item>
                </v-tabs>
            </div>
        </v-navigation-drawer>
        <v-app-bar color="indigo" dark app fixed clipped-left>
            <v-btn icon @click.stop="controlPanelVisibility = !controlPanelVisibility">
                <v-icon>mdi-bus-marker</v-icon>
            </v-btn>
            <v-btn icon @click.stop="statisticsPanelVisibility=!statisticsPanelVisibility">
                <v-icon>mdi-chart-areaspline</v-icon>
            </v-btn>
            <v-toolbar-title>
                {{name}}
            </v-toolbar-title>
        </v-app-bar>
        <v-content>
            <busMap :vehicles="vehicles" :show-bus="showBus" :show-tram="showTram"
                    :stations="stations" :line-count="areaLineMax" :station-radius="areaRadius*areaMultiplier"
                    :show-station-radius="showArea"
                    :districts="districtsAvailable.filter(d=>districtsShown.includes(d.districtName))"
                    :show-draw-tool="showDrawTool" :filter-by-polygons="showDrawTool"
                    v-model="drawnPolygon">
            </busMap>
        </v-content>
    </v-app>
</template>

<script>
    import BusMap from "./map/BusMap";
    import StatisticsPanel from "./map/StatisticsPanel";
    import DistrictRepository from "../repository/DistrictRepository";
    import StationRepository from "../repository/StationRepository";
    import VehicleLocationRepository from "../repository/VehicleLocationRepository";

    export default {
        name: "MapScreen",
        components: {
            BusMap,
            StatisticsPanel
        },
        watch: {
            lines: {
                handler() {
                    this.dataLoad.state = this.dataLoad.interval - 1;
                    this.checkRefresh();
                }
            }
        },
        data: () => ({
            name: "BusFlow",
            linesLabel: "Linie",
            drawnPolygon: [],
            apiUrl: "http://localhost:8282",
            linesHint: "np. 122, E-9, 220...",
            showTramLabel: "Tramwaje",
            showBusLabel: "Autobusy",
            showAreaLabel: "Uwzględnij obszar",
            areaLineMaxLabel: "Przyjęty limit linii na przystanku",
            dataLoadingText: "Odświeżenie danych położenia pojazdów za",
            areaRadiusLabel: "Promień uwzględnianego obszaru",
            exploreLabel: "Eksploruj",
            studyLabel: "Badaj",
            showDrawToolLabel: "Filtruj po figurach",
            districtChoiceLabel: "Obszary dzielnic",
            vehicles: [],
            stations: [],
            lines: [],
            districtsShown: [],
            districtsAvailable: [],
            requestAlert: {
                visible: false,
                type: "info",
                message: ""
            },
            controlPanelVisibility: true,
            statisticsPanelVisibility: false,
            showDrawTool: false,
            showTram: true,
            showBus: true,
            polygons: [],
            areaLineMax: 1,
            showArea: false,
            areaRadius: 300,
            areaMultiplier: 1,
            dataLoad: {
                interval: 10,
                step: 1,
                state: 0,
                infoMessage: "Dane są poprawnie ładowane",
                networkErrorMessage: "Problem z pobieraniem danych! Sprawdź połączenie z siecią...",
                emptyDataMessage: "Brak danych",
                isBeingLoaded: false,
                linesChanged: false,
            }
        }),
        computed: {
            dataLoadingBarValue: function () {
                return this.dataLoad.state / this.dataLoad.interval * 100;
            },
            drawnPoints: function () {
                let pointsArray = [];
                if (this.drawnPolygon.length !== 0) pointsArray.push(this.drawnPolygon);
                this.districtsAvailable.filter(d => this.districtsShown.includes(d.districtName)).forEach(district => pointsArray.push(district.coordinates.map(c => {
                    return {latitude: c.lat, longitude: c.lon}
                })));
                return pointsArray
            }
        },
        methods: {
            requestVehicleData: function () {
                this.dataLoad.isBeingLoaded = true;
                VehicleLocationRepository.findAllByLines(this.lines).then(result => {
                    this.vehicles = result.data;
                    if (this.vehicles.length === 0 && this.lines.length > 0) {
                        this.showAlert(this.dataLoad.emptyDataMessage, "warning");
                    } else {
                        this.showInfoAlertAfterOtherPresentAlert(this.dataLoad.infoMessage);
                    }
                }).catch(() => {
                    this.showAlert(this.dataLoad.networkErrorMessage, "error");
                }).finally(() => {
                    this.dataLoad.isBeingLoaded = false;
                });
            },
            requestStationData() {
                StationRepository.findAllByLines(this.lines).then(result => {
                    this.stations = result.data;
                });
            },
            requestDistrictData() {
                DistrictRepository.findAll().then(result => {
                    this.districtsAvailable = result.data;
                })
            },
            showAlert: function (message, type) {
                this.requestAlert.visible = true;
                this.requestAlert.type = type;
                this.requestAlert.message = message;
            },
            showInfoAlertAfterOtherPresentAlert: function (message) {
                if (this.requestAlert.visible === true && this.requestAlert.type !== "info") {
                    this.requestAlert.type = "info";
                    this.requestAlert.message = message;
                }
            },
            checkRefresh() {
                this.dataLoad.state += this.dataLoad.step;
                if (this.dataLoad.state === this.dataLoad.interval) {
                    this.dataLoad.state = 0;
                    this.requestVehicleData();
                    if (this.dataLoad.linesChanged) this.requestStationData();
                    this.dataLoad.linesChanged = false;
                }
            }
        },
        mounted() {
            this.requestDistrictData();
            window.setInterval(() => {
                this.checkRefresh();
            }, this.dataLoad.step * 1000);
        }
    }
    ;
</script>

<style scoped>
    .controlPanel {
        margin-left: 10px;
        margin-right: 10px;
    }
</style>
