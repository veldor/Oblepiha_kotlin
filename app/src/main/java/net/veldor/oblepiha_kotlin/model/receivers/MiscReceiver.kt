package net.veldor.oblepiha_kotlin.model.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.utils.GatesHandler
import net.veldor.oblepiha_kotlin.model.workers.DeleteBroadcastWorker
import net.veldor.oblepiha_kotlin.model.workers.DeleteMessageWorker
import net.veldor.oblepiha_kotlin.model.workers.MarkBroadcastReadWorker
import net.veldor.oblepiha_kotlin.model.workers.MarkMessageReadWorker

class MiscReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("surprise", "onReceive: have broadcast")
        val type = intent.getStringExtra(EXTRA_ACTION)
        if (type.equals(ACTION_OPEN_GATES)) {
            Log.d("surprise", "onReceive: start open gates")
            GatesHandler().openGates()
        } else if (
            type.equals(ACTION_DELETE_NOTIFICATION) ||
            type.equals(ACTION_DELETE_BROADCAST) ||
            type.equals(ACTION_MARK_BROADCAST_READ) ||
            type.equals(ACTION_MARK_NOTIFICATION_READ)
        ) {
// закрою уведомление, отправившее интент
            val intentId = intent.getIntExtra(NOTIFICATION_ID, 0)
            if (intentId > 0) {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(intentId)
            }
            val data = Data.Builder()
                .putString(MESSAGE_ID, intent.getStringExtra(MESSAGE_ID))
                .build()
            val task: WorkRequest = if (type.equals(ACTION_DELETE_NOTIFICATION)) {
                // run worker, who delete notification
                OneTimeWorkRequest.Builder(DeleteMessageWorker::class.java)
                    .setInputData(data)
                    .build()
            } else if (type.equals(ACTION_MARK_BROADCAST_READ)) {
                OneTimeWorkRequest.Builder(MarkBroadcastReadWorker::class.java)
                    .setInputData(data)
                    .build()
            } else if (type.equals(ACTION_DELETE_BROADCAST)) {
                OneTimeWorkRequest.Builder(DeleteBroadcastWorker::class.java)
                    .setInputData(data)
                    .build()
            } else {
                // run worker, who mark notification read
                OneTimeWorkRequest.Builder(MarkMessageReadWorker::class.java)
                    .setInputData(data)
                    .build()
            }
            WorkManager.getInstance(App.instance).enqueue(task)
        }
    }

    companion object {
        const val EXTRA_ACTION = "action"
        const val MESSAGE_ID = "message id"
        const val NOTIFICATION_ID = "notification id"
        const val ACTION_OPEN_GATES = "open gates"
        const val ACTION_MARK_NOTIFICATION_READ = "mark notification read"
        const val ACTION_MARK_BROADCAST_READ = "mark broadcast read"
        const val ACTION_DELETE_NOTIFICATION = "delete notification"
        const val ACTION_DELETE_BROADCAST = "delete broadcast"
    }
}