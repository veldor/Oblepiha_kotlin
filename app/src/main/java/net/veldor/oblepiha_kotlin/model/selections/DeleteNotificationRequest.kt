package net.veldor.oblepiha_kotlin.model.selections

class DeleteNotificationRequest {
    var command = "delete_notification"
    var id: Int = 0
    var token: String? = null
}