package net.veldor.oblepiha_kotlin.model.selections

class CurrentStatusRequest {
    var command = "get_current_status"
    var token: String? = null
}