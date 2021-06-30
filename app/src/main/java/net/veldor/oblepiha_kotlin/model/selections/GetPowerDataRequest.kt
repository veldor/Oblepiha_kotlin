package net.veldor.oblepiha_kotlin.model.selections

class GetPowerDataRequest {
    var command = "get_power_data"
    var limit = 0
    var offset = 0
    var token: String? = null
}