package net.veldor.oblepiha_kotlin.model.utils

import android.text.format.DateFormat
import androidx.core.text.isDigitsOnly
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import java.util.*

object GrammarHandler {
    private val months = listOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
    private val months_date = listOf("января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря")

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
        val date = DateFormat.format("dd-MM-yyyy", cal).toString()
        val dateArray = date.split("-")
        val parsedDate = StringBuffer()
        parsedDate.append(dateArray[0].toInt())
        parsedDate.append(" ")
        parsedDate.append(months_date[dateArray[1].toInt() - 1])
        parsedDate.append(" ")
        parsedDate.append(dateArray[2])
        parsedDate.append(" в ")
        parsedDate.append(DateFormat.format("HH:mm:ss", cal).toString())
        return parsedDate.toString()
    }
    fun convertTime(last_time: String): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = last_time.toLong() * 1000
        val date = DateFormat.format("dd-MM-yyyy", cal).toString()
        val dateArray = date.split("-")
        val parsedDate = StringBuffer()
        parsedDate.append(dateArray[0].toInt())
        parsedDate.append(" ")
        parsedDate.append(months_date[dateArray[1].toInt() - 1])
        parsedDate.append(" ")
        parsedDate.append(dateArray[2])
        return parsedDate.toString()
    }

    fun timeToString(lastCheckTime: Long): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = lastCheckTime * 1000
        val date = DateFormat.format("dd-MM-yyyy", cal).toString()
        val dateArray = date.split("-")
        val parsedDate = StringBuffer()
        parsedDate.append(dateArray[0].toInt())
        parsedDate.append(" ")
        parsedDate.append(months_date[dateArray[1].toInt() - 1])
        parsedDate.append(" ")
        parsedDate.append(dateArray[2])
        parsedDate.append(" в ")
        parsedDate.append(DateFormat.format("HH:mm:ss", cal).toString())
        return parsedDate.toString()
    }

    fun showPrice(price: Int): String {
        return String.format(Locale.ENGLISH, App.instance.getString(R.string.ruble_template), price.toDouble() / 100)
    }

    fun convertMonth(month: String): String {
        val arr = month.split("-")
        return arr[0] + "\n" + months[arr[1].toInt() - 1]
    }

    fun convertWatt(value: String): String {
        return String.format(Locale.ENGLISH, App.instance.getString(R.string.title_power_data), value)
    }

    fun getBillNumberFromString(message: String): String? {
         var start = message.indexOf("Счёт №") + 6
        var number = message.substring(start, message.indexOf(" ", start))
        if(number.isDigitsOnly()){
            return number
        }
        start = message.indexOf("счёту №") + 7
        number = message.substring(start, message.indexOf(".", start))
        if(number.isDigitsOnly()){
            return number
        }
        return null
    }
}