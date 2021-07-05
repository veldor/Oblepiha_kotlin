package net.veldor.oblepiha_kotlin.model.selections

import android.util.Log
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler

class GetPowerDataResponse {
    fun modify() {
        if(list.isNotEmpty()){
            list.forEach{
                if(it != null){
                    it.shortMonth = it.month
                    it.month = GrammarHandler.convertMonth(it.month)
                    it.newPowerData = it.newPowerData + App.instance.getString(R.string.kw)
                    it.oldPowerData = it.oldPowerData + App.instance.getString(R.string.kw)
                    it.inLimitSumm = it.inLimitSumm + App.instance.getString(R.string.kw)
                    it.overLimitSumm = it.overLimitSumm + App.instance.getString(R.string.kw)
                    it.inLimitPay = it.inLimitPay + App.instance.getString(R.string.rub)
                    it.overLimitPay = it.overLimitPay + App.instance.getString(R.string.rub)
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