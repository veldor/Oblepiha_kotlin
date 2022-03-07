package net.veldor.oblepiha_kotlin.model.selections

class ReadNotificationRequest {
    var command = "read_notification"
    var id: Int = 0
    var token: String? = null
}