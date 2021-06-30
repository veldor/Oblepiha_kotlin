package net.veldor.oblepiha_kotlin.model.selections

class ChangeAlertModeRequest {
    var command = "change_alert_mode"
    var token: String? = null
    var mode: Boolean? = null
}