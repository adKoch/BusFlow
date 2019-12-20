import Vue from 'vue'
import VueRouter from 'vue-router'
import vuetify from './plugins/vuetify';
import {Icon} from 'leaflet'
import 'leaflet/dist/leaflet.css'
import axios from "axios"
import VueAxios from "vue-axios"
import MapView from "./components/View/MapView";

Vue.config.productionTip = false;
Vue.use(VueAxios, axios);
Vue.use(VueRouter);
// this part resolves an issue where the markers would not appear
delete Icon.Default.prototype._getIconUrl;

Icon.Default.mergeOptions({
    iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
    iconUrl: require('leaflet/dist/images/marker-icon.png'),
    shadowUrl: require('leaflet/dist/images/marker-shadow.png')
});
new Vue({
    vuetify,
    render: h => h(MapView)
}).$mount('#app');