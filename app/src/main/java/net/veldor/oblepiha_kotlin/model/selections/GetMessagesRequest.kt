package net.veldor.oblepiha_kotlin.model.selections

class GetMessagesRequest {
    var command = "get_messages"
    var limit = 0
    var offset = 0
    var token: String? = null
}