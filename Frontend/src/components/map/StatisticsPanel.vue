<template>
    <v-container class="ma-4 mx-auto"
                 max-width="400">
        <v-select :items="aggregationTypes"
                  v-model="chosenAggregationType"
                  outlined
                  :item-text="type=>type.name"
                  :item-value="type=>type.type"
                  :label="aggregationTypeLabel"/>
        <v-expansion-panels v-if="chosenAggregationType!==2">
            <v-expansion-panel>
                <v-expansion-panel-header>{{datetimePanelHeader}}</v-expansion-panel-header>
                <v-expansion-panel-content>
                    <v-select :items="aggregationPeriod"
                              v-model="chosenAggregationPeriod"
                              :label="aggregationPeriodLabel"
                              :item-text="item=>item.text"
                              :item-value="item=>item.value"/>
                    <typed-date-picker :max-date="getMaxDate()" :min-date="getMinDate()"
                                       v-model="aggregationDate"/>
                    <typed-time-picker v-model="aggregationTime"/>
                </v-expansion-panel-content>
            </v-expansion-panel>
        </v-expansion-panels>
        <v-label v-if="!dataParamsReady">
            {{dataParamsNotReadyLabel}}
        </v-label>
        <v-layout class="justify-center">
            <v-btn top
                   class="ma-4"
                   @click="getChartData"
                   color="indigo"
                   dark
                   :disabled="!chosenParamsReady || !dataParamsReady"
                   :loading="loadingChartData"
                   rounded> {{generateChartLabel}}
            </v-btn>

        </v-layout>
        <v-layout class="justify-center">
            <GChart v-if="generated"
                    class="ma-6"
                    :data="gChartData"
                    :type="chosenChartType"
                    :title="chartLabel"
            />
        </v-layout>
        <v-layout class="justify-center">
            <v-select v-if="generated"
                      :items="availableChartTypes"
                      v-model="chosenChartType"
                      outlined
                      :item-text="type=>type.text"
                      :item-value="type=>type.value"
                      :label="chartTypeLabel"/>
        </v-layout>
    </v-container>
</template>

<script>
    import {GChart} from "vue-google-charts"
    import TypedDatePicker from "../statistic/TypedDatePicker";
    import TypedTimePicker from "../statistic/TypedTimePicker";
    import StatisticRepository from "../../repository/StatisticRepository";
    import {stationCountByLines} from "../../core/StationAggregation";

    export default {
        name: "StatisticsPanel",
        components: {
            TypedTimePicker,
            TypedDatePicker,
            GChart
        }, watch: {
            chartData: {
                handler(newVal) {
                    let gData = [];
                    gData.push(['Linia', 'Ilość']);
                    newVal.forEach(obj => {
                        gData.push([obj.label, obj.value])
                    });
                    this.gChartData = gData
                }
            }
        }, computed: {
            chosenParamsReady: function () {
                return this.chosenAggregationType !== null && (this.chosenAggregationType === 2 ||
                    (
                        this.aggregationTime !== null &&
                        this.aggregationDate !== null &&
                        this.chosenAggregationPeriod !== null))
            },
            dataParamsReady: function () {
                return typeof this.shownPolygonsPoints !== 'undefined' &&
                    this.shownPolygonsPoints !== null &&
                    this.shownPolygonsPoints.length !== 0 &&
                    (this.chosenAggregationType !== 2 || (typeof this.shownStations !== 'undefined' && this.shownStations !== null && this.shownStations.length !== 0))
            }
        },
        props: ['shownLines', 'shownPolygonsPoints', 'shownStations'],
        data: () => ({
            sampleSize: 16,
            chartData: [],
            chosenChartType: 'ColumnChart',
            chosenAggregationType: 2,
            chosenAggregationPeriod: null,
            aggregationDate: null,
            aggregationTime: null,
            maxDateDayOffset: 0,
            minDateDayOffset: 1,
            generated: false,
            loadingChartData: false,
            availableChartTypes: [
                {text: 'Wykres kolumnowy', value: 'ColumnChart'},
                {text: 'Wykres liniowy', value: 'LineChart'},
                {text: 'Wykres kołowy', value: 'PieChart'},
                {text: 'Histogram', value: 'Histogram'},
                {text: 'Pola', value: 'AreaChart'},
                {text: 'Tabela', value: 'Table'}
            ],
            chartTypeLabel: "Typ wykresu",
            dataParamsNotReadyLabel: "Aby wygenerować wykres potrzebny jest obszar i przystanki...",
            generateChartLabel: "Generuj wykres",
            aggregationPeriod: [
                {text: "60min", value: 3600},
                {text: "3h", value: 10800},
                {text: "6h", value: 21600},
                {text: "12h", value: 43200},
                {text: "24h", value: 86400}
            ],
            aggregationPeriodLabel: "Okres",
            aggregationTypeLabel: "Typ agregacji",
            datetimePanelHeader: "Zakres czasowy",
            datePickerLabel: "Data początkowa",
            timePickerLabel: "Czas początkowy",
            chartLabel: "",
            gChartData: [],
            aggregationTypes: [
                //{name: "Ilość pojazdów w czasie", type: 0},
                //{name: "Ilość linii w czasie", type: 1},
                {name: "Liczba przystanków dla linii", type: 2}
            ],
            chartTypes: ["lineChart", "barChart"],
            label: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"],
            value: [1, 3, 5, 2, 13, 12, 56, 12, 3, 9, 4, 1],
        }),
        methods: {
            stationCountByLines,
            getMaxDate() {
                let date = new Date();
                date.setDate(date.getDate() + this.maxDateDayOffset);
                return date.toISOString().substr(0, 10)
            },
            getMinDate() {
                let date = new Date();
                date.setDate(date.getDate() - this.minDateDayOffset);
                return date.toISOString().substr(0, 10)
            },
            getFormattedDate(date) {
                return `${date.toISOString().substring(0, 10)} ${date.toISOString().substring(11, 19)}`;
            },
            getChartData() {
                if (this.chosenAggregationType === 0) this.getVehicleCountData();
                else if (this.chosenAggregationType === 1) this.getVehicleLineCountData();
                else if (this.chosenAggregationType === 2) this.getStationLineCountData();
            },
            getVehicleCountData() {
                const latitudes = this.shownPolygonsPoints.map(point => point.latitude);
                const longitudes = this.shownPolygonsPoints.map(point => point.longitude);
                const start = `${this.aggregationDate} ${this.aggregationTime}`;
                const end = this.getEndDateFromStartAndPeriod();
                const interval = this.chosenAggregationPeriod / this.sampleSize;
                const lines = this.shownLines === null || this.shownLines.length < 1 ? null : this.shownLines;

                this.loadingChartData = true;

                StatisticRepository.findVehicleCountStatistic(latitudes, longitudes, start, end, interval, lines).then(result => {
                    this.chartData = result.data;
                    this.generated = true;
                }).finally(() => {
                    this.loadingChartData = false
                })
            },
            getStationLineCountData() {
                this.chartData = this.formatLineCountData(stationCountByLines(this.shownPolygonsPoints, this.shownStations));
                this.generated = true;
            },
            getVehicleLineCountData() {
                const latitudes = this.shownPolygonsPoints.map(point => point.latitude);
                const longitudes = this.shownPolygonsPoints.map(point => point.longitude);
                const start = `${this.aggregationDate} ${this.aggregationTime}`;
                const end = this.getEndDateFromStartAndPeriod();
                const interval = this.chosenAggregationPeriod / this.sampleSize;

                const lines = this.shownLines === null || this.shownLines.length < 1 ? null : this.shownLines;

                this.loadingChartData = true;

                StatisticRepository.findVehicleLineCountStatistic(latitudes, longitudes, start, end, interval, lines).then(result => {
                    this.chartData = this.formatVehicleCountData(result.data);
                    this.generated = true;
                }).finally(() => {
                    this.loadingChartData = false
                })
            },
            formatVehicleCountData(data) {
                if (typeof data === 'undefined' || data === null || data.length === 0) return [];
                return data.map(d => {
                    return {
                        label: this.formatRetVehicleCountDate(d.start),
                        value: d.count
                    }
                })
            },
            formatRetVehicleCountDate(date) {
                return `${date[3]}${date[4] > 10 ? '0' + date[4] : date[4]}${date[5] > 10 ? '0' + date[5] : date[5]}`
            },
            formatLineCountData(data) {

                data = Object.keys(data).map(key => {
                    return {
                        label: key,
                        value: data[key]
                    }
                });
                data = data.sort((a, b) => (a.value > b.value) ? -1 : 1);
                if (data.length >= this.sampleSize) {
                    let additional = 0;
                    data.slice(this.sampleSize, data.length).forEach(d => additional += d.value);
                    data = data.slice(0, this.sampleSize - 1);
                    data.push({
                        label: 'Inne',
                        value: additional
                    })
                }
                return data
            },
            getSecondsFromChosenTime() {
                const times = this.aggregationTime.split(":");
                return times[0] * 3600 + times[1] * 60
            },
            getEpochFromChosenTime() {
                return new Date(this.aggregationDate).getTime() / 1000 + this.getSecondsFromChosenTime()
            },
            getEndDateFromStartAndPeriod() {
                return this.getFormattedDate(new Date((this.getEpochFromChosenTime() + this.chosenAggregationPeriod) * 1000));
            }
        }
    }
</script>

<style scoped>

</style>