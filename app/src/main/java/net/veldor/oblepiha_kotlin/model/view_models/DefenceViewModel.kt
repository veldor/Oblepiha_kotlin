package net.veldor.oblepiha_kotlin.model.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.database.dao.PowerDao
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData
import net.veldor.oblepiha_kotlin.model.selections.ApiCurrentStatusResponse
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import net.veldor.oblepiha_kotlin.model.workers.LogoutWorker

class DefenceViewModel : ViewModel() {
    fun logout() {
        // запущу рабочего, который выполнит выход из учётной записи
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val checkStatusWork = OneTimeWorkRequest.Builder(LogoutWorker::class.java)
            .addTag(LogoutWorker.ACTION).setConstraints(constraints).build()
        WorkManager.getInstance(App.instance).enqueue(checkStatusWork)
    }

    fun checkStatus() {
        viewModelScope.launch (Dispatchers.IO ){
            if (!App.instance.preferences.isUserUnknown) {
                val connector = MyConnector()
                val responseText: String = connector.requestCurrentStatus()
                Log.d("surprise", "DefenceViewModel.kt 36: $responseText")
                // разберу ответ
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                val response: ApiCurrentStatusResponse = gson.fromJson(
                    responseText,
                    ApiCurrentStatusResponse::class.java
                )
                // save power data to db
                val dao: PowerDao = App.instance.database.powerDao()
                val newData = PowerData()
                newData.data = response.last_data
                newData.timestamp = response.last_time
                // check have no current value in db
                val existent: PowerData? = dao.getByTimestamp(newData.timestamp)
                if (existent == null) {
                    // check previous data
                    val previous = dao.lastRegistered
                    if(previous != null){
                        newData.previousData = previous.data
                    }
                    else{
                        newData.previousData = newData.data
                    }
                    dao.insert(newData)
                }
                App.instance.mCurrentStatusResponse.postValue(response)
                App.instance.preferences.saveLastCheckTime()
                App.instance.preferences.saveLastResponse(response)
            }
        }
    }

    fun switchAlertMode(state: Boolean) {
        // notify action in progress
        actionInProgress.postValue(true)
        viewModelScope.launch(Dispatchers.IO) {
            // enabling alert
            val connector = MyConnector()
            while (true) {
                val responseText: String = connector.setAlertMode(state)
                // отправлю запрос на изменение режима
                // разберу ответ
                if (responseText.isNotEmpty()) {
                    val builder = GsonBuilder()
                    val gson = builder.create()
                    val parsedResponse = gson.fromJson(
                        responseText,
                        ApiCurrentStatusResponse::class.java
                    )
                    // проверю, если запрос поставлен в очередь- буду ожидать подтверждения
                    if (parsedResponse != null && parsedResponse.status == "success") {
                        if (waitForConfirm()) {
                            statusChangeRequestAccepted.postValue(true)
                            actionInProgress.postValue(false)
                            break
                        }
                    } else {
                        // не удалось поменять статус
                        if (parsedResponse != null) {
                            Log.d(
                                "surprise",
                                "SetAlertModeWorker doWork 50: " + parsedResponse.message
                            )
                        }
                        statusChangeRequestAccepted.postValue(false)
                        actionInProgress.postValue(false)
                        break
                    }
                }
            }
        }
    }

    private fun waitForConfirm(): Boolean {
        val connector = MyConnector()
        while (true) {
            // буду раз в секунду отправлять запросы на сервер и проверять, выполнено ли переключение
            if (connector.checkModeChanged()) {
                Log.d("surprise", "SetAlertModeWorker waitForConfirm 67: change status confirmed")
                return true
            }
            Log.d(
                "surprise",
                "SetAlertModeWorker waitForConfirm 70: waiting for change status confirm"
            )
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    val actionInProgress: MutableLiveData<Boolean> = MutableLiveData(false)
    var statusChangeRequestAccepted = MutableLiveData<Boolean>()
}