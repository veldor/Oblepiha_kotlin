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
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("surprise", "MyFirebaseMessagingService onMessageReceived 31: some message received!")
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
        }
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(
                TAG, "Message Notification Body: " + remoteMessage.notification!!
                    .body
            )
        }
    }

    override fun onNewToken(token: String) {
        App.instance.preferences.firebaseToken = token
    } // [END on_new_token]

    companion object {
        private const val TAG = "surprise"
    }
}