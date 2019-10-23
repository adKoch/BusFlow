import Vue from 'vue'
import App from './App.vue'
import vuetify from './plugins/vuetify';
import {Icon} from 'leaflet'
import 'leaflet/dist/leaflet.css'
import axios from "axios"
import VueAxios from "vue-axios"

Vue.config.productionTip = false;
Vue.use(VueAxios, axios);
// this part resolve an issue where the markers would not appear
delete Icon.Default.prototype._getIconUrl;

Icon.Default.mergeOptions({
    iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
    iconUrl: require('leaflet/dist/images/marker-icon.png'),
    shadowUrl: require('leaflet/dist/images/marker-shadow.png')
});
new Vue({
    vuetify,
    render: h => h(App)
}).$mount('#app');