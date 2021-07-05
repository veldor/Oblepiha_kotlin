package net.veldor.oblepiha_kotlin.model.selections

import android.util.Log
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler

class BillDetailsResponse {
    fun modify() {
        if (entities.isNotEmpty()) {
            entities.forEach {
                it!!.toPay = GrammarHandler.showPrice(it.toPay.toInt())
            }
        }
        if(transactions.isNotEmpty()){
            transactions.forEach {
                it!!.transactionSumm = GrammarHandler.showPrice(it.transactionSumm.toInt())
                it.transactionDate = GrammarHandler.timeToString(it.transactionDate)
            }
        }
    }

    var status = ""
    var entities: List<BillEntity?> = listOf()
    var transactions: List<BillTransaction?> = listOf()
    var count = 0
}