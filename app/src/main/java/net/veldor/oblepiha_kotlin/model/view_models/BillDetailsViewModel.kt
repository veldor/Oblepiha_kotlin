package net.veldor.oblepiha_kotlin.model.view_models

import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.selections.*
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import net.veldor.oblepiha_kotlin.model.utils.PrefsBackup
import java.util.*

class BillDetailsViewModel : ViewModel() {
    fun loadAdditionalBillInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val connector = MyConnector()
            val responseText: String = connector.requestBillInfo(selectedForDetails!!.id)
            if (responseText.isNotEmpty()) {
                Log.d("surprise", "BillDetailsViewModel.kt 25: $responseText")
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                val response: BillDetailsResponse = gson.fromJson(
                    responseText,
                    BillDetailsResponse::class.java
                )
                response.modify()
                billInfo.postValue(response)
            }
        }
    }

    val billInfo = MutableLiveData<BillDetailsResponse?>()

    companion object {
        var selectedForDetails: BillListItem? = null
    }
}