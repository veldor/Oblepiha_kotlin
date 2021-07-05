package net.veldor.oblepiha_kotlin.model.workers

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.database.dao.PowerDao
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData
import net.veldor.oblepiha_kotlin.model.selections.ApiCurrentStatusResponse
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import net.veldor.oblepiha_kotlin.model.utils.RawDataHandler

class CheckStatusWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        if (!App.instance.preferences.isUserUnknown) {
            val connector = MyConnector()
            val responseText: String = connector.requestCurrentStatus()
            if(responseText.isNotEmpty()){
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
                val handler = RawDataHandler(response.raw_data)
                val used: String = handler.getUsed(response.channel, response.initial_value)
                newData.data = used
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
                return Result.success()
            }
        }
        return Result.failure()
    }
}