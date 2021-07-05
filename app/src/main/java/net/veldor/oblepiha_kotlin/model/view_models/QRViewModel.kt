package net.veldor.oblepiha_kotlin.model.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.model.selections.*
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import java.util.*

class QRViewModel : ViewModel() {

    fun loadQR(billId: String?) {
        if (billId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val connector = MyConnector()
                val responseText: String = connector.requestBillQR(billId)
                Log.d("surprise", "QRViewModel.kt 21: $responseText")
                if (responseText.isNotEmpty()) {
                    val builder = GsonBuilder()
                    val gson: Gson = builder.create()
                    val response: BillQRResponse = gson.fromJson(
                        responseText,
                        BillQRResponse::class.java
                    )
                    qr.postValue(response.qr_data)
                }
            }
        }
    }

    val qr = MutableLiveData<String>()
}