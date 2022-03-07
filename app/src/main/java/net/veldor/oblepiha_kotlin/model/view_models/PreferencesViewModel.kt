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
import net.veldor.oblepiha_kotlin.model.selections.AccrualsStatusResponse
import net.veldor.oblepiha_kotlin.model.selections.ApiCurrentStatusResponse
import net.veldor.oblepiha_kotlin.model.utils.ApiHandler
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import net.veldor.oblepiha_kotlin.model.utils.PrefsBackup
import java.util.*

class PreferencesViewModel : ViewModel() {
    fun createBackup(dl: DocumentFile) {
        viewModelScope.launch(Dispatchers.IO) {
            PrefsBackup().doBackup(dl)
        }
    }

    fun restoreBackup(dl: DocumentFile) {
        viewModelScope.launch(Dispatchers.IO) {
            PrefsBackup().restoreBackup(dl)
        }
    }

    fun notifyPowerUseShowStateChanged(state: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            ApiHandler().switchPowerUseState(state)
        }
    }

}