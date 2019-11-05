<template>
    <v-app>
        <v-navigation-drawer v-model="controlPanelVisibility" app fixed clipped>
            <div class="controlPanel">
                <v-row>
                    <v-checkbox v-model="mapControls.showBus" :label="showBusLabel" class="mx-2"></v-checkbox>
                    <v-checkbox v-model="mapControls.showTram" :label="showTramLabel" class="mx-2"></v-checkbox>
                </v-row>
                <v-combobox
                        v-model="mapControls.lines"
                        chips
                        multiple
                        :label="linesLabel"
                        :hint="linesHint"
                ></v-combobox>
            </div>
        </v-navigation-drawer>
        <v-app-bar color="indigo" dark app fixed clipped-left>
            <v-app-bar-nav-icon @click.stop="controlPanelVisibility = !controlPanelVisibility"></v-app-bar-nav-icon>
            <v-toolbar-title>{{name}}</v-toolbar-title>
        </v-app-bar>
        <v-content>
            <busMap v-bind:controls="mapControls"></busMap>
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
            linesHint: "np. 122, E-9, 220...",
            showTramLabel: "Tramwaje",
            showBusLabel: "Autobusy",
            controlPanelVisibility: null,
            mapControls: {
                showTram: true,
                showBus: true,
                lines: [],
            }
        }),
    };
</script>

<style scoped>
    .controlPanel{
        margin-left: 10px;
        margin-right: 10px;
    }
</style>
