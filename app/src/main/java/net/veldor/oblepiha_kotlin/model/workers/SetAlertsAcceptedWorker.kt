package net.veldor.oblepiha_kotlin.model.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import net.veldor.oblepiha_kotlin.model.utils.MyConnector

class SetAlertsAcceptedWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {
    override fun doWork(): Result {
        val mConnector = MyConnector()
        while (true) {
            // отправлю запрос на изменение режима
            // разберу ответ
            if (mConnector.setAlertsHandled()) {
                break
            }
            try {
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        return Result.success()
    }

    companion object {
        const val ACTION = "accept alerts"
    }
}