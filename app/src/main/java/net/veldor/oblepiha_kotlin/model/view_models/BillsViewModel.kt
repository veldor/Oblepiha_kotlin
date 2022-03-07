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
import net.veldor.oblepiha_kotlin.model.delegates.BillInfoDelegate
import net.veldor.oblepiha_kotlin.model.selections.*
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

    fun requireBillDetails(billId: String?, delegate: BillInfoDelegate) {
        if (billId != null) {
            Log.d("surprise", "BillsViewModel.kt 36 requireBillDetails request info for $billId")
            // require bill info
            viewModelScope.launch(Dispatchers.IO) {
                val connector = MyConnector()
                val responseText: String = connector.requestBill(billId.toInt())
                Log.d("surprise", "BillsViewModel.kt 41 requireBillDetails $responseText")
                if (responseText.isNotEmpty()) {
                    val builder = GsonBuilder()
                    val gson: Gson = builder.create()
                    val response: BillResponse = gson.fromJson(
                        responseText,
                        BillResponse::class.java
                    )
                    if (response.bill != null) {
                        delegate.billInfoReceived(response.bill)
                    } else {
                        Toast.makeText(App.instance, "Can't find bill info!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    val state = MutableLiveData<BillsStateResponse?>()
}