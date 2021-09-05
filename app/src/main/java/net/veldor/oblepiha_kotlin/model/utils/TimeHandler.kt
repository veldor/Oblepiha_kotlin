package net.veldor.oblepiha_kotlin.model.utils

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
}