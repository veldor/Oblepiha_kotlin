package net.veldor.oblepiha_kotlin.model.selections

class GetBillsDataRequest {
    var command = "get_bills_data"
    var limit = 0
    var offset = 0
    var isPayed = false
    var token: String? = null
}