package net.veldor.oblepiha_kotlin.model.selections

import android.util.Log
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler

class GetBillsDataResponse {
    fun modify() {
        if(list.isNotEmpty()){
            list.forEach{
                if(it != null){
                    it.depositUsed = ((it.depositUsed.toDouble() * 100).toInt()).toString()
                    it.toDeposit = ((it.toDeposit.toDouble() * 100).toInt()).toString()
                    it.creationTime = GrammarHandler.convertTime(it.creationTime)
                }
            }
        }
    }

    var status = ""
    var list: List<BillListItem?> = listOf()
    var count = 0
}