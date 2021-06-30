package net.veldor.oblepiha_kotlin.model.selections

class LoginRequest {
    var command = "login"
    var login: String = ""
    var password: String = ""
    var token: String? = null
}