package net.veldor.oblepiha_kotlin.model.selections

class PowerUseRequest {
    var command = "get_power_use_status"
    var month: Int = 0
    var year: Int = 0
    var token: String? = null
}