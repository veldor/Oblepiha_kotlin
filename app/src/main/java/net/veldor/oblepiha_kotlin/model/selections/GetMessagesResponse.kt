package net.veldor.oblepiha_kotlin.model.selections

import android.util.Log
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler
import net.veldor.oblepiha_kotlin.model.utils.TimeHandler

class GetMessagesResponse {

    fun modify() {
        if(list.isNotEmpty()){
            list.forEach{
                if(it != null){
                    it.time_of_creation = TimeHandler().timestampToDatetime(it.time_of_creation.toInt())
                }
            }
        }
    }

    var status = ""
    var list: List<MessageItem?> = listOf()
    var count = 0
}