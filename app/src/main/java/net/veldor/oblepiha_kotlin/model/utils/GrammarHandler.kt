package net.veldor.oblepiha_kotlin.model.utils

import android.text.format.DateFormat
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import java.util.*

object GrammarHandler {
    val months = listOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")

    fun handleTemperature(temp: String): Int {
        return if (temp.toInt() > 127) {
            temp.toInt() - 256
        } else temp.toInt()
    }

    fun handleWatt(last_data: String): String {
        return last_data.substring(
            0,
            last_data.length - 1
        ) + "." + last_data.substring(last_data.length - 1)
    }

    fun timeToString(last_time: String): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = last_time.toLong() * 1000
        return DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString()
    }

    fun timeToString(lastCheckTime: Long): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = lastCheckTime * 1000
        return DateFormat.format("dd-MM-yyyy HH:mm:ss", cal).toString()
    }

    fun showPrice(price: Int): String {
        return String.format(Locale.ENGLISH, "%.2f rub", price.toDouble() / 100)
    }

    fun convertMonth(month: String): String {
        val arr = month.split("-")
        return arr[0] + "\n" + months[arr[1].toInt() - 1]
    }

    fun convertWatt(value: String): String {
        return String.format(Locale.ENGLISH, App.instance.getString(R.string.title_power_data), value)
    }
}