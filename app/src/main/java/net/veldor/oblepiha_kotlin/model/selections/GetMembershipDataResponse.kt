package net.veldor.oblepiha_kotlin.model.selections

import android.util.Log
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler

class GetMembershipDataResponse {
    fun modify() {
        if(list.isNotEmpty()){
            list.forEach{
                if(it != null){
                    it.payed = GrammarHandler.showPrice(it.payed.toInt())
                    // count accrued
                    it.accrued = GrammarHandler.showPrice(it.fixed_part.toInt() * 100 + (it.square_part.toInt() * it.counted_square.toInt()))
                    Log.d("surprise", "GetMembershipDataResponse.kt 14: ${it.accrued}")
                }
            }
        }
    }

    var status = ""
    var list: List<MembershipListItem?> = listOf()
    var count = 0
}