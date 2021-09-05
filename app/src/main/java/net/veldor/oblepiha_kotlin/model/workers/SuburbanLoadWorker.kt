package net.veldor.oblepiha_kotlin.model.workers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.format.DateFormat
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.selections.ApiCurrentStatusResponse
import net.veldor.oblepiha_kotlin.model.selections.SuburbanResponse
import net.veldor.oblepiha_kotlin.model.selections.SuburbanSchedule
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import net.veldor.oblepiha_kotlin.model.utils.SuburbanHandler
import java.util.*

class SuburbanLoadWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        // request data from server
        if (!App.instance.preferences.isUserUnknown) {
            var haveAccess = false;
            // if have access to sd-card
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val writeResult = App.instance.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                if (writeResult == PackageManager.PERMISSION_GRANTED) {
                    haveAccess = true
                }
            }
            else{
                haveAccess = true
            }
            if(haveAccess){
                // try load previous schedule
                val existentFromSchedule = SuburbanHandler(null).load("from")
                val existentToSchedule = SuburbanHandler(null).load("to")
                if(existentFromSchedule != null && existentToSchedule != null){
                    // check actuality
                    val currentTime = Calendar.getInstance()
                    val date = DateFormat.format("yyyy-MM-dd", currentTime).toString()
                    if(existentFromSchedule.date == date && existentToSchedule.date == date){
                        Log.d("surprise", "doWork: schedule is actual!")
                        App.instance.incomingSuburbans.postValue(existentFromSchedule)
                        App.instance.outgoingSuburbans.postValue(existentToSchedule)
                        return Result.success()
                    }
                    else{
                        Log.d("surprise", "doWork: reload schedule")
                    }
                }
            }
            val connector = MyConnector()
            val responseText: String = connector.requestSuburban()
            if (responseText.isNotEmpty()) {
                // разберу ответ
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                val response: SuburbanResponse = gson.fromJson(
                    responseText,
                    SuburbanResponse::class.java
                )
                // parse segments
                val fromSegment = gson.fromJson(
                    response.from,
                    SuburbanSchedule::class.java
                )
                // parse segments
                val toSegment = gson.fromJson(
                    response.to,
                    SuburbanSchedule::class.java
                )
                fromSegment.date = response.date
                toSegment.date = response.date
                // save objects to local storage
                if(haveAccess) {
                    SuburbanHandler(fromSegment).save("from")
                    SuburbanHandler(toSegment).save("to")
                }
                App.instance.incomingSuburbans.postValue(fromSegment)
                App.instance.outgoingSuburbans.postValue(toSegment)
                return Result.success()

            }
        }
        return Result.failure()
    }
}