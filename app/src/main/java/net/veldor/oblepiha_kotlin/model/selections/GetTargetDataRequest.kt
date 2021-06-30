package net.veldor.oblepiha_kotlin.model.selections

class GetTargetDataRequest {
    var command = "get_target_data"
    var limit = 0
    var offset = 0
    var token: String? = null
}