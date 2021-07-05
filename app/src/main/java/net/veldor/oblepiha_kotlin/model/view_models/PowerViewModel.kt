package net.veldor.oblepiha_kotlin.model.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import java.util.*

class PowerViewModel : ViewModel() {
    private val _spendForMonth = MutableLiveData<String>().apply {
        val dao = App.instance.database.powerDao()
        val time = Calendar.getInstance()
        time.set(Calendar.HOUR_OF_DAY, 0)
        time.set(Calendar.MINUTE, 0)
        time.set(Calendar.SECOND, 0)
        time.set(Calendar.MILLISECOND, 0)
        time.set(Calendar.DAY_OF_MONTH, 1)
        val values = dao.getFromTimestamp(time.timeInMillis / 1000)
        var spendValue = 0.0
        if(values.isNotEmpty()){
            values.forEach{
                spendValue += it!!.data.toDouble()- it.previousData.toDouble()
            }
        }
        Log.d("surprise", "PowerViewModel.kt 27: spend $spendValue")
        value = String.format(Locale.ENGLISH, App.instance.getString(R.string.kw_template), spendValue)
    }
    val spendForMonth: LiveData<String> = _spendForMonth

    private val _spendForDay = MutableLiveData<String>().apply {
        val dao = App.instance.database.powerDao()
        val time = Calendar.getInstance()
        time.set(Calendar.HOUR_OF_DAY, 0)
        time.set(Calendar.MINUTE, 0)
        time.set(Calendar.SECOND, 0)
        val values = dao.getFromTimestamp(time.timeInMillis / 1000)
        var spendValue = 0.0
        if(values.isNotEmpty()){
            values.forEach{
                spendValue += it!!.data.toDouble() - it.previousData.toDouble()
            }
        }
        value = String.format(Locale.ENGLISH, App.instance.getString(R.string.kw_template), spendValue)
    }
    val spendForDay: LiveData<String> = _spendForDay
}