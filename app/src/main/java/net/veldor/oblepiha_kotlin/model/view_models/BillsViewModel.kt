package net.veldor.oblepiha_kotlin.model.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.model.selections.ApiCurrentStatusResponse
import net.veldor.oblepiha_kotlin.model.selections.BillsStateResponse
import net.veldor.oblepiha_kotlin.model.selections.MembershipListItem
import net.veldor.oblepiha_kotlin.model.utils.MyConnector

class BillsViewModel : ViewModel() {
    fun updateInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val connector = MyConnector()
            val responseText: String = connector.requestBillState()
            if (responseText.isNotEmpty()) {
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                val response: BillsStateResponse = gson.fromJson(
                    responseText,
                    BillsStateResponse::class.java
                )
                state.postValue(response)
            }
        }
    }

    val state = MutableLiveData<BillsStateResponse?>()
}