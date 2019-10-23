<template>
    <v-app>
        <v-navigation-drawer v-model="controlPanelVisibility" app fixed clipped>
            <div class="controlPanel">
                <v-checkbox v-model="mapControls.showTooltips" :label="showTooltipCheckboxLabel"></v-checkbox>
                <v-combobox
                        v-model="mapControls.lines"
                        chips
                        multiple
                        label="Linie"
                        hint="np. 122, E-9, 220..."
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
            showTooltipCheckboxLabel: "Pokaż linie",
            controlPanelVisibility: null,
            mapControls: {
                showTooltips: true,
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
