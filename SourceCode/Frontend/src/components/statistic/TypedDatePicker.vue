<template>
    <v-menu
            v-model="dateMenu"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            min-width="290px">
        <template v-slot:activator="{ on }">
            <v-text-field
                    v-model="aggregationDate"
                    :label="datePickerLabel"
                    readonly
                    v-on="on"/>
        </template>
        <v-date-picker
                v-model="aggregationDate"
                class="mt-4"
                show-current
                locale="pl-pl"
                :first-day-of-week="1"
                :max="maxDate"
                :min="minDate"/>
    </v-menu>
</template>

<script>
    export default {
        name: "TypedDatePicker"
        ,data :() =>({
            dateMenu: false,
            datePickerLabel: "Data początkowa",
            aggregationDate: null,
        }),
        props:['maxDate','minDate'],
        watch:{
            aggregationDate:{
                handler(newVal){
                    this.$emit('input', newVal)
                }
            }
        }
    }
</script>