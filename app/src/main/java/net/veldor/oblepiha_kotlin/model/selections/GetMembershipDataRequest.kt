package net.veldor.oblepiha_kotlin.model.selections

class GetMembershipDataRequest {
    var command = "get_membership_data"
    var limit = 0
    var offset = 0
    var token: String? = null
}