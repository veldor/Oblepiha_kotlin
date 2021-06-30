package net.veldor.oblepiha_kotlin.model.selections

import android.util.Log
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler

class GetPowerDataResponse {
    fun modify() {
        if(list.isNotEmpty()){
            list.forEach{
                if(it != null){
                    it.month = GrammarHandler.convertMonth(it.month)
                    it.difference = GrammarHandler.convertWatt(it.difference)
                    it.payed = GrammarHandler.showPrice(it.payed.toInt())
                    it.totalPay = GrammarHandler.showPrice(it.totalPay.toInt())
                }
            }
        }
    }

    var status = ""
    var list: List<PowerListItem?> = listOf()
    var count = 0
}