package net.veldor.oblepiha_kotlin.model.utils

import android.content.Context
import android.util.Log
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.selections.Segment
import net.veldor.oblepiha_kotlin.model.selections.SuburbanSchedule
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

class SuburbanHandler(val suburbanSchedule: SuburbanSchedule?) {
    fun getNext(): String {
        // найду электричку, время которой следующее за текущим
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        val currentMinute = currentTime.get(Calendar.MINUTE)
        suburbanSchedule?.segments?.forEach {
            // get hour
            val suburbanTime = it.departure!!.substring(11, 19)
            val timeArray = suburbanTime.split(":")
            val hour = timeArray[0].toInt()
            val minute = timeArray[1].toInt()
            if (hour == currentHour) {
                if (minute > currentMinute) {
                    return String.format(
                        Locale.ENGLISH,
                        "%s\nчерез %d мин.",
                        suburbanTime,
                        minute - currentMinute
                    )
                }
            } else if (hour > currentHour) {
                val spendMinutes = 60 - currentMinute + minute
                if(spendMinutes / 60 > 0){
                    return String.format(
                        Locale.ENGLISH,
                        "%s\nчерез %d ч. %d мин.",
                        suburbanTime,
                        hour - currentHour,
                        spendMinutes % 60
                    )
                }
                else{
                    val hourDiff = hour - currentHour - 1
                    if(hourDiff > 0){
                        return String.format(
                            Locale.ENGLISH,
                            "%s\nчерез %d ч. %d мин.",
                            suburbanTime,
                            hourDiff,
                            spendMinutes
                        )
                    }
                    else{
                        return String.format(
                            Locale.ENGLISH,
                            "%s\nчерез %d мин.",
                            suburbanTime,
                            spendMinutes
                        )
                    }
                }
            }
        }
        return "Уже не сегодня";
    }

    fun save(name: String) {
        val fos: FileOutputStream = App.instance.openFileOutput(name, Context.MODE_PRIVATE)
        val os = ObjectOutputStream(fos)
        os.writeObject(suburbanSchedule)
        os.close()
        fos.close()
        Log.d("surprise", "save: file saved")
    }

    fun load(name: String): SuburbanSchedule?{
        return try {
            val fis: FileInputStream = App.instance.openFileInput(name)
            val inputStream = ObjectInputStream(fis)
            val schedule: SuburbanSchedule = inputStream.readObject() as SuburbanSchedule
            inputStream.close()
            fis.close()
            schedule
        } catch (e: Exception){
            Log.d("surprise", "load: ${e.stackTrace}")
            null
        }
    }

    fun getInfo(get: Segment?): String {
        if(get != null){
            // найду электричку, время которой следующее за текущим
            val currentTime = Calendar.getInstance()
            val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
            val currentMinute = currentTime.get(Calendar.MINUTE)
            // get hour
            val suburbanTime = get.departure!!.substring(11, 19)
            val timeArray = suburbanTime.split(":")
            val hour = timeArray[0].toInt()
            val minute = timeArray[1].toInt()
            if (hour == currentHour) {
                if (minute > currentMinute) {
                    return String.format(
                        Locale.ENGLISH,
                        "%s\nчерез %d мин.",
                        suburbanTime,
                        minute - currentMinute
                    )
                }
            } else if (hour > currentHour) {
                val spendMinutes = 60 - currentMinute + minute
                if(spendMinutes / 60 > 0){
                    return String.format(
                        Locale.ENGLISH,
                        "%s\nчерез %d ч. %d мин.",
                        suburbanTime,
                        hour - currentHour,
                        spendMinutes % 60
                    )
                }
                else{
                    val hourDiff = hour - currentHour - 1
                    if(hourDiff > 0){
                        return String.format(
                            Locale.ENGLISH,
                            "%s\nчерез %d ч. %d мин.",
                            suburbanTime,
                            hourDiff,
                            spendMinutes
                        )
                    }
                    else{
                        return String.format(
                            Locale.ENGLISH,
                            "%s\nчерез %d мин.",
                            suburbanTime,
                            spendMinutes
                        )
                    }
                }
            }
            return "$suburbanTime\n Уже ушла";
        }
        return ""
    }
}