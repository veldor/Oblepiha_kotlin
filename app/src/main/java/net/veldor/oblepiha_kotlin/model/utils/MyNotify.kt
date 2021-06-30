package net.veldor.oblepiha_kotlin.model.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.view.AlarmActivity

class MyNotify {
    //public static final int CHECKER_NOTIFICATION = 3;
    private val mNotificationManager: NotificationManager?
    private val mContext: App
    private var mLastNotificationId = 100
    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mNotificationManager != null) {
                // создам канал статического уведомления для фонового получения данных
                var nc = NotificationChannel(
                    CHECKER_CHANNEL_ID,
                    mContext.getString(R.string.checker_channel_description),
                    NotificationManager.IMPORTANCE_LOW
                )
                nc.description = mContext.getString(R.string.checker_channel_description)
                nc.enableLights(false)
                nc.setSound(null, null)
                nc.enableVibration(false)
                mNotificationManager.createNotificationChannel(nc)

                // создам канал уведомлений о изменении стостояния защиты
                nc = NotificationChannel(
                    DEFENCE_STATE_CNANGED_CHANNEL_ID,
                    mContext.getString(R.string.defence_state_change_channel_description),
                    NotificationManager.IMPORTANCE_HIGH
                )
                nc.description =
                    mContext.getString(R.string.defence_state_change_channel_description)
                nc.enableLights(true)
                nc.lightColor = Color.GREEN
                nc.enableVibration(true)
                mNotificationManager.createNotificationChannel(nc)
                // создам канал уведомлений о срабатывании защиты
                nc = NotificationChannel(
                    ALERT_RECEIVED_CHANNEL_ID,
                    mContext.getString(R.string.alert_received_channel_description),
                    NotificationManager.IMPORTANCE_HIGH
                )
                nc.description = mContext.getString(R.string.alert_received_channel_description)
                nc.enableLights(true)
                nc.lightColor = Color.RED
                nc.enableVibration(true)
                mNotificationManager.createNotificationChannel(nc)
            }
        }
    }

    //    public Notification createCheckingNotification() {
    //        Intent notificationIntent = new Intent(App.getInstance(), MainActivity.class);
    //        PendingIntent contentIntent = PendingIntent.getActivity(App.getInstance(),
    //                0, notificationIntent,
    //                PendingIntent.FLAG_CANCEL_CURRENT);
    //        // создам интент, который переключит таймер на пищевое окно
    //        NotificationCompat.Builder checkerNotification = new NotificationCompat.Builder(mContext, CHECKER_CHANNEL_ID)
    //                .setStyle(new NotificationCompat.BigTextStyle().bigText(mContext.getString(R.string.checker_message)))
    //                .setContentTitle(null)
    //                .setContentIntent(contentIntent)
    //                .setSmallIcon(R.drawable.ic_defence)
    //                .setOngoing(true);
    //        Notification notification = checkerNotification.build();
    //        mNotificationManager.notify(CHECKER_NOTIFICATION, notification);
    //        return notification;
    //    }
    fun showStatusChangedNotification(current_status: Boolean) {
        val text: String =
            if (current_status) mContext.getString(R.string.defence_enabled) else mContext.getString(
                R.string.defence_disabled
            )
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(mContext, DEFENCE_STATE_CNANGED_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_defence)
                .setContentTitle(mContext.getString(R.string.defence_state_changed_title))
                .setPriority(Notification.PRIORITY_MAX)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true)
        val notification = notificationBuilder.build()
        mNotificationManager!!.notify(mLastNotificationId, notification)
        mLastNotificationId++
    }

    fun showAlertNotification(alert: String?) {
        val fullScreenIntent = Intent(App.instance, AlarmActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(
            App.instance, 0,
            fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(App.instance, ALERT_RECEIVED_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_defence)
                .setContentTitle("Сработала сигнализация")
                .setContentText(alert)
                .setColor(Color.RED)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL) // Use a full-screen intent only for the highest-priority alerts where you
                // have an associated activity that you would like to launch after the user
                // interacts with the notification. Also, if your app targets Android 10
                // or higher, you need to request the USE_FULL_SCREEN_INTENT permission in
                // order for the platform to invoke this notification.
                .setFullScreenIntent(fullScreenPendingIntent, true)
        val incomingCallNotification = notificationBuilder.build()
        mNotificationManager!!.notify(mLastNotificationId, incomingCallNotification)
        mLastNotificationId++
    }

    companion object {
        private const val CHECKER_CHANNEL_ID = "checker"
        private const val DEFENCE_STATE_CNANGED_CHANNEL_ID = "congrats"
        private const val ALERT_RECEIVED_CHANNEL_ID = "alert received"
    }

    init {
        mContext = App.instance
        mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // создам каналы уведомлений
        createChannels()
    }
}