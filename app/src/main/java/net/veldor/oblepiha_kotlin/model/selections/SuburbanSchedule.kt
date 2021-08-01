package net.veldor.oblepiha_kotlin.model.selections

import java.io.Serializable

class SuburbanSchedule:Serializable {
    var date: String? = null
    var pagination: Map<String, String>? = null
    var segments: List<Segment>? = null
}

class Segment:Serializable {
    var arrival: String? = null
    var departure: String? = null
}