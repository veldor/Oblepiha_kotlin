package net.veldor.oblepiha_kotlin.model.selections

import java.io.Serializable

class AccrualsStatusResponse:Serializable {
    var status: String = ""
    var totalDuty: Int = 0
    var powerDuty: Int = 0
    var membershipDuty: Int = 0
    var targetDuty: Int = 0
}