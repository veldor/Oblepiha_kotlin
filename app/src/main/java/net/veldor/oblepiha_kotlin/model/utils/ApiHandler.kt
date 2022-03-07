package net.veldor.oblepiha_kotlin.model.utils

class ApiHandler {
    fun switchPowerUseState(value: Boolean) {
        val connector = MyConnector()
        val responseText: String = connector.setPowerUseShow(value)
    }
}