package net.veldor.oblepiha_kotlin.model.utils

import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.workers.CheckStatusWorker

class MyFirebaseMessagingService : FirebaseMessagingService() {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("surprise", "MyFirebaseMessagingService.kt 21 onMessageReceived i receive message!!")
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data
            if (data.containsKey("type")) {
                val type = data["type"]
                if (type == "alert") {
                    // получено оповещение о тревоге, покажу окно
                    App.instance.notifier.showAlertNotification("Сработала защита")
                    App.instance.showAlertWindow(data["raw data"])
                } else if (type == "change") {
                    Log.d(
                        "surprise",
                        "MyFirebaseMessagingService onMessageReceived 67: accept state change message"
                    )
                    // поменялся статус, оповещу об этом
                    val state = data["state"]
                    App.instance.notifier.showStatusChangedNotification(state == "on")
                    // внеочередно сделаю запрос нового состояния
                    // запущу рабочего, который выполнит проверку данных
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    val checkStatusWork = OneTimeWorkRequest.Builder(
                        CheckStatusWorker::class.java
                    ).setConstraints(constraints).build()
                    WorkManager.getInstance(App.instance).enqueue(checkStatusWork)
                    WorkManager.getInstance(App.instance)
                        .getWorkInfoByIdLiveData(checkStatusWork.id)
                }
            }
            else if(data.containsKey("action")){
                val action = data["action"]
                if(action == "powerUseShowStateChanged"){
                    App.instance.notifier.showPowerUseShowStateChangedNotification(data["value"], data["notification_id"])
                }
                else if(action == "newPowerUseInfo"){
                    val spend = data["spend"]
                    val spendForMonth = data["spendForMonth"]
                    val monthCost = data["monthCost"]
                    val currentTimestamp = data["currentTimestamp"]
                    Log.d("surprise", "MyFirebaseMessagingService.kt 63 onMessageReceived $currentTimestamp")
                    val previousTimestamp = data["previousTimestamp"]
                    Log.d("surprise", "MyFirebaseMessagingService.kt 65 onMessageReceived $previousTimestamp")
                    val periodCost = data["periodCost"]
                    App.instance.notifier.notifyUsedData(spend, spendForMonth, monthCost, currentTimestamp, previousTimestamp, periodCost, data["notification_id"])
                    return
                }
            }
            else if(data.containsKey("notification")){
                val action = data["notification"]
                if(action == "new_bill"){
                    App.instance.notifier.showContentNotification(data)
                }
                else if(action == "broadcast"){
                    App.instance.notifier.showBroadcastNotification(data)
                }
                else if(action == "transaction_confirm"){
                    App.instance.notifier.showContentNotification(data)
                }
            }
        }
        // Check if message contains a notification payload.
        else if (remoteMessage.notification != null) {
            App.instance.notifier.showTopicNotification(remoteMessage.notification!!)
        }
    }

    override fun onNewToken(token: String) {
        App.instance.preferences.firebaseToken = token
    }

    companion object {
        private const val TAG = "surprise"
    }
}