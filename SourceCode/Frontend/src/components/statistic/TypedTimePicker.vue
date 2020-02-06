<template>
    <v-menu
            v-model="timeMenu"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            min-width="290px">
        <template v-slot:activator="{ on }">
            <v-text-field
                    v-model="aggregationTime"
                    :label="timePickerLabel"
                    readonly
                    v-on="on"/>
        </template>
        <v-time-picker
                v-model="aggregationTime"
                scrollable
                color="indigo"
                format="24hr"/>
    </v-menu>
</template>

<script>
    export default {
        name: "TypedTimePicker",
        data: () => ({
            timePickerLabel: "Czas początkowy",
            timeMenu: false,
            aggregationTime: null,
        }), watch: {
            aggregationTime: {
                handler() {
                    this.$emit('input', this.aggregationTime)
                }
            }
        }
    }
</script>

<style scoped>

</style>