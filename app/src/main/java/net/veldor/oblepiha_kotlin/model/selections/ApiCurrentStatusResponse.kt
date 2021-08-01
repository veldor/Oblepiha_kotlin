package net.veldor.oblepiha_kotlin.model.selections

import java.io.Serializable

class ApiCurrentStatusResponse:Serializable {
    var status: String = ""
    var message: String = ""
    var owner_io: String = ""
    var cottage_number: String = ""
    var current_status = false
    var temp: String = ""
    var last_time: Long = 0
    var connection_time: String = ""
    var last_data: String = ""
    var alerts: ArrayList<String>? = null
    var raw_data: String = ""
    var initial_value: String = ""
    var perimeter_state: String = ""
    var have_defence = 0
    var total_duty = 0
    var opened_bills: String = ""
    var channel = 0
}