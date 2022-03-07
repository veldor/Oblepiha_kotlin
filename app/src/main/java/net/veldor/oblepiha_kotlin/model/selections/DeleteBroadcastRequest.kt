package net.veldor.oblepiha_kotlin.model.selections

class DeleteBroadcastRequest {
    var command = "delete_broadcast"
    var id: String = ""
    var token: String? = null
}