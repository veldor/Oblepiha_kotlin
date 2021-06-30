package net.veldor.oblepiha_kotlin.model.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.selections.AccrualsStatusResponse
import net.veldor.oblepiha_kotlin.model.selections.ApiCurrentStatusResponse
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import java.util.*

class AccrualsViewModel : ViewModel() {
    fun checkStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!App.instance.preferences.isUserUnknown) {
                val connector = MyConnector()
                val responseText: String = connector.requestAccrualsStatus()
                Log.d("surprise", "AccrualsViewModel.kt 24: $responseText")
                if(responseText.isNotEmpty()){
                    // разберу ответ
                    val builder = GsonBuilder()
                    val gson: Gson = builder.create()
                    val response: AccrualsStatusResponse = gson.fromJson(
                        responseText,
                        AccrualsStatusResponse::class.java
                    )
                    _status.postValue(response)
                }
            }
        }
    }

    private val _status = MutableLiveData<AccrualsStatusResponse>()
    val status: LiveData<AccrualsStatusResponse> = _status
}