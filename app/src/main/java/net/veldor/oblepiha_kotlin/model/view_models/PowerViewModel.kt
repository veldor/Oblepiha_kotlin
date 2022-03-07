package net.veldor.oblepiha_kotlin.model.view_models

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.model.selections.UsedPowerItem
import net.veldor.oblepiha_kotlin.model.selections.UsedPowerResponse
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import net.veldor.oblepiha_kotlin.model.utils.TimeHandler

class PowerViewModel : ViewModel() {

    private var selectedMonth = TimeHandler().getCurrentMonth()
    private var selectedYear = TimeHandler().getCurrentYear()
    var month: MutableLiveData<String> =
        MutableLiveData(TimeHandler.MONTHS[TimeHandler().getCurrentMonth()])
    var year: MutableLiveData<Int> = MutableLiveData(TimeHandler().getCurrentYear())
    var list: MutableLiveData<List<UsedPowerItem>> = MutableLiveData()

    fun requestData() {
        viewModelScope.launch(Dispatchers.IO) {
            // request from server
            val connector = MyConnector()
            val responseText: String = connector.requestPowerUse(selectedYear, selectedMonth)
            if (responseText.isNotEmpty()) {
                Log.d("surprise", "PowerViewModel.kt 30 requestData $responseText")
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                val response: UsedPowerResponse = gson.fromJson(
                    responseText,
                    UsedPowerResponse::class.java
                )
                if (response.status == "success") {
                    list.postValue(response.usedDataList)
                    spendForMonth.postValue(response.spendForMonth + " кВт ("  + response.monthPay + " р.)")
                    spendForDay.postValue(response.spendForDay + " кВт ("  + response.dayPay + " р.)")
                }
            }
        }
    }

    fun loadNextMonth() {
        if (selectedMonth == 11) {
            selectedMonth = 0
            ++selectedYear
        } else {
            ++selectedMonth
        }
        month.postValue(TimeHandler.MONTHS[selectedMonth])
        year.postValue(selectedYear)
        requestData()
    }

    fun loadPrevMonth() {
        if (selectedMonth == 0) {
            selectedMonth = 11
            --selectedYear
        } else {
            --selectedMonth
        }
        month.postValue(TimeHandler.MONTHS[selectedMonth])
        year.postValue(selectedYear)
        requestData()
    }

    fun requestCurrentData() {
        selectedMonth = TimeHandler().getCurrentMonth()
        selectedYear = TimeHandler().getCurrentYear()
        month.postValue(TimeHandler.MONTHS[selectedMonth])
        year.postValue(selectedYear)
        requestData()
    }

    fun markPowerNotificationRead(id: String?) {
        if (id != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val connector = MyConnector()
                val response = connector.requestNotificationRead(id.toInt())
                if (response.isEmpty()) {
                    Toast.makeText(
                        App.instance,
                        App.instance.getString(R.string.wrong_request_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    val spendForMonth: MutableLiveData<String> = MutableLiveData()


    val spendForDay: MutableLiveData<String> = MutableLiveData()

    init {
        requestData()
    }
}