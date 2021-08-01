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

class SuburbansViewModel : ViewModel() {
    fun updateInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            // handle suburbans
        }
    }

    companion object{

        lateinit var destination: String
    }
}