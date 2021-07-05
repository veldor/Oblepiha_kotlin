package net.veldor.oblepiha_kotlin.model.selections

import android.util.Log
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler

class EntityPaysResponse {
    fun modify() {
        if(list.isNotEmpty()){
            list.forEach {
                it!!.paymentDate = GrammarHandler.timeToString(it.paymentDate)
                it.sum = GrammarHandler.showPrice(it.sum.toInt())
                it.billId = App.instance.getString(R.string.bill_prefix) + it.billId
            }
        }
    }

    var status = ""
    var list: List<PaymentItem?> = listOf()
}