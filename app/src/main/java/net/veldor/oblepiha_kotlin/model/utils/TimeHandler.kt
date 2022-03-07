package net.veldor.oblepiha_kotlin.model.utils

import java.text.SimpleDateFormat
import java.util.*

class TimeHandler {
    companion object {
        val MONTHS = arrayListOf(
            "январь",
            "февраль",
            "март",
            "апрель",
            "май",
            "июнь",
            "июль",
            "август",
            "сентябрь",
            "октябрь",
            "ноябрь",
            "декабрь"
        )
    }

    public fun getCurrentMonth(): Int{
        val time = Calendar.getInstance()
        return time.get(Calendar.MONTH)
    }
    public fun getCurrentYear(): Int{
        val time = Calendar.getInstance()
        return time.get(Calendar.YEAR)
    }

    fun timestampToDatetime(time: Int): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH)
        val netDate = Date(time.toLong() * 1000)
        return sdf.format(netDate)
    }
}