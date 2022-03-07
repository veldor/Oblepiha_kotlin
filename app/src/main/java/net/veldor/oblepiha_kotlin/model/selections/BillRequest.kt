package net.veldor.oblepiha_kotlin.model.selections

class BillRequest {
    var command = "get_bill"
    var id: Int = 0
    var token: String? = null
}