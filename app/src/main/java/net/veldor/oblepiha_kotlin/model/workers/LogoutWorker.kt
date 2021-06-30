package net.veldor.oblepiha_kotlin.model.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.GsonBuilder
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.selections.ApiLoginResponse
import net.veldor.oblepiha_kotlin.model.utils.MyConnector

class LogoutWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val token: String? = App.instance.preferences.token
        val firebaseToken: String? = App.instance.preferences.firebaseToken
        val connector = MyConnector()
        val responseText: String = connector.requestLogout(token, firebaseToken)
        // разберу ответ
        if (responseText.isNotEmpty()) {
            val builder = GsonBuilder()
            val gson = builder.create()
            val response: ApiLoginResponse =
                gson.fromJson(responseText, ApiLoginResponse::class.java)
            if (response.status.equals(MyConnector.STATUS_SUCCESS)) {
                return Result.success()
            }
        }
        return Result.failure()
    }

    companion object {
        const val ACTION = "logout"
    }
}