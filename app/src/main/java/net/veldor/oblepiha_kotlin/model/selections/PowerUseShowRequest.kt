package net.veldor.oblepiha_kotlin.model.selections

class PowerUseShowRequest {
    var command = "set_power_use_state"
    var state: Int = 0
    var token: String? = null
}