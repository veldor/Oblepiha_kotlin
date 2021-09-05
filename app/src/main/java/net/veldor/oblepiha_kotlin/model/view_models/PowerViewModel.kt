package net.veldor.oblepiha_kotlin.model.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
            if(responseText.isNotEmpty()){
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                val response: UsedPowerResponse = gson.fromJson(
                    responseText,
                    UsedPowerResponse::class.java
                )
                if(response.status == "success"){
                    list.postValue(response.usedDataList)
                    spendForMonth.postValue(response.spendForMonth + " кВт")
                    spendForDay.postValue(response.spendForDay + " кВт")
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

    val spendForMonth: MutableLiveData<String> = MutableLiveData()


    val spendForDay: MutableLiveData<String> = MutableLiveData()

    init {
        requestData()
    }
}