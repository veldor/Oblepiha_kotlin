package net.veldor.oblepiha_kotlin.model.selections

import android.util.Log
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler

class GetTargetDataResponse {
    fun modify() {
        if(list.isNotEmpty()){
            list.forEach{
                if(it != null){
                    it.payed = (it.payed.toInt() +  (it.payed_outside.toInt() * 100)).toString()
                    it.payed = GrammarHandler.showPrice(it.payed.toInt())
                    // count accrued
                    it.accrued = GrammarHandler.showPrice(it.fixed_part.toInt() * 100 + (it.square_part.toInt() * it.counted_square.toInt()))
                    it.fixed_part = GrammarHandler.showPrice(it.fixed_part.toInt() * 100)
                    it.square_part = GrammarHandler.showPrice(it.square_part.toInt() * 100)
                    it.counted_square = it.counted_square + App.instance.getString(R.string.meter)
                }
            }
        }
    }

    var status = ""
    var list: List<TargetListItem?> = listOf()
    var count = 0
}