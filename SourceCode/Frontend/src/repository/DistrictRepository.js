import axios from "axios"

const apiUrl="http://localhost:8282";
const route="/district_geography";

function requestDistrictData() {
    return axios.get(apiUrl+route)
}

export default {
    findAll(){
        return requestDistrictData()
    }
}