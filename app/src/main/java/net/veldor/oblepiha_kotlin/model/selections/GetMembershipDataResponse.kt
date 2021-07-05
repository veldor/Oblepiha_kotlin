package net.veldor.oblepiha_kotlin.model.selections

import android.util.Log
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler

class GetMembershipDataResponse {
    fun modify() {
        if(list.isNotEmpty()){
            list.forEach{
                if(it != null){
                    it.payed = GrammarHandler.showPrice(it.payed.toInt())
                    // count accrued
                    it.accrued = GrammarHandler.showPrice(it.fixed_part.toInt() + (it.square_part.toFloat() / 100.0 * it.counted_square.toInt()).toInt())
                    it.fixed_part = GrammarHandler.showPrice(it.fixed_part.toInt())
                    it.square_part = GrammarHandler.showPrice(it.square_part.toInt())
                    it.counted_square = it.counted_square + App.instance.getString(R.string.meter)
                }
            }
        }
    }

    var status = ""
    var list: List<MembershipListItem?> = listOf()
    var count = 0
}