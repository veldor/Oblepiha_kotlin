package net.veldor.oblepiha_kotlin.model.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.veldor.oblepiha_kotlin.App
import java.util.*

class PowerViewModel : ViewModel() {
    private val _spendForMonth = MutableLiveData<String>().apply {
        val dao = App.instance.database.powerDao()
        val time = Calendar.getInstance()
        time.set(Calendar.HOUR_OF_DAY, 0)
        time.set(Calendar.MINUTE, 0)
        time.set(Calendar.SECOND, 0)
        time.set(Calendar.DAY_OF_MONTH, 1)
        val values = dao.getFromTimestamp(time.timeInMillis)
        var spendValue = 0L
        if(values.isNotEmpty()){
            values.forEach{
                spendValue += it!!.data.toLong() - it.previousData.toLong()
            }
        }
        value = String.format(Locale.ENGLISH, "%d kWt*H", spendValue)
    }
    val spendForMonth: LiveData<String> = _spendForMonth

    private val _spendForDay = MutableLiveData<String>().apply {
        val dao = App.instance.database.powerDao()
        val time = Calendar.getInstance()
        time.set(Calendar.HOUR_OF_DAY, 0)
        time.set(Calendar.MINUTE, 0)
        time.set(Calendar.SECOND, 0)
        val values = dao.getFromTimestamp(time.timeInMillis)
        var spendValue = 0L
        if(values.isNotEmpty()){
            values.forEach{
                spendValue += it!!.data.toLong() - it.previousData.toLong()
            }
        }
        value = String.format(Locale.ENGLISH, "%d kWt*H", spendValue)
    }
    val spendForDay: LiveData<String> = _spendForDay
}