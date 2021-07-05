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

class AccrualsTargetDetailsViewModel : ViewModel() {
    val paysInfo = MutableLiveData<EntityPaysResponse?>()
    fun requestPays() {
        viewModelScope.launch(Dispatchers.IO) {
            val connector = MyConnector()
            val responseText: String =
                connector.requestPays(selectedForDetails!!.year, "target")
            if (responseText.isNotEmpty()) {
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                val response: EntityPaysResponse = gson.fromJson(
                    responseText,
                    EntityPaysResponse::class.java
                )
                response.modify()
                paysInfo.postValue(response)
            }
        }
    }
    companion object{
        var selectedForDetails:TargetListItem? = null
    }
}