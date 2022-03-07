package net.veldor.oblepiha_kotlin.model.workers

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.model.database.dao.PowerDao
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData
import net.veldor.oblepiha_kotlin.model.receivers.MiscReceiver
import net.veldor.oblepiha_kotlin.model.selections.ApiCurrentStatusResponse
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import net.veldor.oblepiha_kotlin.model.utils.RawDataHandler

class MarkBroadcastReadWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val connector = MyConnector()
        val response = connector.requestBroadcastRead(
            inputData.getString(MiscReceiver.MESSAGE_ID)!!
        )
        if (response.isEmpty()) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    App.instance,
                    App.instance.getString(R.string.wrong_request_message),
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    App.instance,
                    App.instance.getString(R.string.notification_read_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        return Result.success()
    }
}