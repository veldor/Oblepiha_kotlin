package net.veldor.oblepiha_kotlin.model.selections

class LogoutRequest {
    var command = "logout"
    var token: String? = ""
    var firebase_token: String? = ""
}