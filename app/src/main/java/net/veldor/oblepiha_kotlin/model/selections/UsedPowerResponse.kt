package net.veldor.oblepiha_kotlin.model.selections

import android.util.Log
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler

class UsedPowerResponse {
    var status: String? = null
    var spendForDay: String? = null
    var spendForMonth: String? = null
    var monthPay: String? = null
    var dayPay: String? = null
    var usedDataList: List<UsedPowerItem> = listOf()
}