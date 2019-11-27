<template>
    <v-app>
        <v-navigation-drawer v-model="controlPanelVisibility" app fixed clipped>
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
                <v-card ref="requestStatusCard"
                        loader-height="7"
                        :loader="dataLoad.isBeingLoaded"
                        class="mt-4">
                    <v-container>
                        <v-row align="center">
                            <v-col>
                                <v-card-text>
                                    {{dataLoadingText}} {{dataLoad.interval-dataLoad.state}}s
                                </v-card-text>
                            </v-col>
                            <v-col>
                                <v-progress-circular v-bind:value="dataLoadingBarValue"
                                                     size="60"
                                                     width="7"
                                                     color="indigo"
                                                     class="text-center"
                                                     rotate="270"></v-progress-circular>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card>
                <v-tabs centered color="indigo" class="mt-4">
                    <v-tab>{{exploreLabel}}</v-tab>
                    <v-tab>{{studyLabel}}</v-tab>
                    <v-tab-item>
                        <v-row>
                            <v-checkbox v-model="mapControls.showBus"
                                        :label="showBusLabel"
                                        class="mx-2"></v-checkbox>
                            <v-checkbox v-model="mapControls.showTram"
                                        :label="showTramLabel"
                                        class="mx-2"></v-checkbox>
                        </v-row>
                        <v-combobox v-model="mapControls.lines"
                                    chips
                                    multiple
                                    :label="linesLabel"
                                    :hint="linesHint">
                        </v-combobox>
                    </v-tab-item>
                    <v-tab-item>
                        <v-row>
                            <v-checkbox v-model="mapControls.showArea"
                                        :label="showAreaLabel"
                                        class="mx-2"></v-checkbox>
                        </v-row>
                        <v-row>
                            <v-form>
                                <v-text-field
                                        v-model="mapControls.areaLineMax"
                                        type="number"
                                        class="mx-5"
                                        :label="areaLineMaxLabel"
                                        :disabled="!mapControls.showArea"
                                />
                                <v-text-field
                                        v-model="mapControls.areaRadius"
                                        type="number"
                                        class="mx-5"
                                        :label="areaRadiusLabel"
                                        :disabled="!mapControls.showArea"
                                />
                            </v-form>
                            <v-btn-toggle v-model="mapControls.areaMultiplier" tile group color="indigo" class="ma-5">
                                <v-btn :value=1 :disabled="!mapControls.showArea">
                                    <span>m</span>
                                </v-btn>
                                <v-btn :value=1000 :disabled="!mapControls.showArea">
                                    <span>km</span>
                                </v-btn>
                            </v-btn-toggle>
                        </v-row>
                    </v-tab-item>
                </v-tabs>
            </div>
        </v-navigation-drawer>
        <v-app-bar color="indigo" dark app fixed clipped-left>
            <v-app-bar-nav-icon @click.stop="controlPanelVisibility = !controlPanelVisibility"></v-app-bar-nav-icon>
            <v-toolbar-title>{{name}}</v-toolbar-title>
        </v-app-bar>
        <v-content>
            <busMap v-bind:controls="mapControls">
            </busMap>
        </v-content>
    </v-app>
</template>

<script>
    import BusMap from "./components/BusMap.vue";

    export default {
        name: "App",
        components: {
            busMap: BusMap
        },
        data: () => ({
            name: "BusFlow",
            linesLabel: "Linie",
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
            requestAlert: {
                visible: false,
                type: "info",
                message: ""
            },
            controlPanelVisibility: null,
            mapControls: {
                showTram: true,
                showBus: true,
                areaLineMax: 15,
                showArea: false,
                areaRadius: 150,
                areaMultiplier: 1,
                lines: [],
                vehicles: [],
                stations: [],
            },
            dataLoad: {
                interval: 10,
                step: 1,
                state: 0,
                infoMessage: "Dane są poprawnie ładowane",
                networkErrorMessage: "Problem z pobieraniem danych! Sprawdź połączenie z siecią...",
                emptyDataMessage: "Brak danych",
                isBeingLoaded: false,
            }
        }),
        computed: {
            dataLoadingBarValue: function () {
                return this.dataLoad.state / this.dataLoad.interval * 100;
            }
        },
        methods: {
            getLinesApiUrl: function (lines) {
                return `${this.apiUrl}/vehicle_location?lines=`.concat(lines);
            },
            getStationsApiUrl: function (lines) {
                return `${this.apiUrl}/station?lines=`.concat(lines);
            },
            requestVehicleData: function () {
                this.dataLoad.isBeingLoaded = true;
                this.$http.get(this.getLinesApiUrl(this.mapControls.lines)).then(result => {

                    this.mapControls.vehicles = result.data;
                    if (this.mapControls.vehicles.length === 0) {
                        this.showAlert(this.dataLoad.emptyDataMessage, "warning");
                    } else {
                        this.showInfoAlertAfterOtherPresentAlert(this.dataLoad.infoMessage);
                    }
                    this.dataLoad.isBeingLoaded = false;
                }).catch(() => {
                    this.showAlert(this.dataLoad.networkErrorMessage, "error");
                    this.dataLoad.isBeingLoaded = false;
                });
            },
            requestStationData() {
                this.$http.get(this.getStationsApiUrl(this.mapControls.lines)).then(result => {
                    this.mapControls.stations = result.data;
                });
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
            }
        },
        mounted() {
            this.requestVehicleData();
            this.requestStationData();
            window.setInterval(() => {
                this.dataLoad.state += this.dataLoad.step;
                if (this.dataLoad.state === this.dataLoad.interval) {
                    this.dataLoad.state = 0;
                    this.requestVehicleData();
                    this.requestStationData();
                }
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
