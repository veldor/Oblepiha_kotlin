package net.veldor.oblepiha_kotlin.model.utils

import android.annotation.SuppressLint
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
import com.google.firebase.messaging.RemoteMessage
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.model.receivers.MiscReceiver
import net.veldor.oblepiha_kotlin.view.AlarmActivity
import net.veldor.oblepiha_kotlin.view.ContentActivity
import net.veldor.oblepiha_kotlin.view.MessagesActivity
import java.util.*

class MyNotify {
    private val mNotificationManager: NotificationManager?
    private val mContext: App = App.instance
    private var mLastNotificationId = 100
    private var mActionId = 100
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
                    DEFENCE_STATE_CHANGED_CHANNEL_ID,
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

                // Добавлю канал для уведомлений о загружаемых файлах
                nc = NotificationChannel(
                    FILE_LOAD_CHANNEL_ID,
                    App.instance.getString(R.string.file_load_channel),
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                nc.description = App.instance.getString(R.string.file_load_channel)
                nc.enableLights(false)
                nc.enableVibration(false)
                nc.setSound(null, null)
                mNotificationManager.createNotificationChannel(nc)
                // Добавлю канал для уведомлений о загруженных файлах
                nc = NotificationChannel(
                    FILE_LOADED_CHANNEL_ID,
                    App.instance.getString(R.string.file_load_channel),
                    NotificationManager.IMPORTANCE_HIGH
                )

                nc.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                nc.description = App.instance.getString(R.string.file_load_channel)
                nc.enableLights(true)
                nc.lightColor = Color.GREEN
                nc.enableVibration(true)
                mNotificationManager.createNotificationChannel(nc)
            }
        }
    }

    fun showStatusChangedNotification(current_status: Boolean) {
        val text: String =
            if (current_status) mContext.getString(R.string.defence_enabled) else mContext.getString(
                R.string.defence_disabled
            )
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(mContext, DEFENCE_STATE_CHANGED_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_defence)
                .setContentTitle(mContext.getString(R.string.defence_state_changed_title))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setStyle(NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true)
        val notification = notificationBuilder.build()
        mNotificationManager!!.notify(mLastNotificationId, notification)
        mLastNotificationId++
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showAlertNotification(alert: String?) {
        val fullScreenIntent = Intent(App.instance, AlarmActivity::class.java)

        val fullScreenPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                App.instance,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                App.instance,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

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

    fun showTopicNotification(notification: RemoteMessage.Notification) {
        val newNotification =
            NotificationCompat.Builder(App.instance, DEFENCE_STATE_CHANGED_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_notification_mono)
                .setContentTitle(notification.title)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLights(Color.YELLOW, 500, 500)
                .setChannelId(DEFENCE_STATE_CHANGED_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(
                    NotificationCompat.BigTextStyle().bigText(notification.body)
                )
                .build()
        mNotificationManager!!.notify(mLastNotificationId, newNotification)
        mLastNotificationId++
    }

    fun createFileLoadNotification(): Notification {
        val mDownloadScheduleBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            App.instance,
            FILE_LOAD_CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_baseline_arrow_downward_24)
            .setContentTitle(App.instance.getString(R.string.file_loading_title))
            .setOngoing(true)
            .setContentText(App.instance.getString(R.string.file_loading_body))
            .setProgress(0, 0, true)
            .setAutoCancel(false)
        return mDownloadScheduleBuilder.build()
    }

    fun getNextIdentifier(): Int {
        mLastNotificationId++
        return mLastNotificationId
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showPowerUseShowStateChangedNotification(s: String?, notificationId: String?) {        // add actions for buttons on notification
        val markReadIntent = Intent(App.instance, MiscReceiver::class.java)
        markReadIntent.putExtra(
            MiscReceiver.EXTRA_ACTION,
            MiscReceiver.ACTION_MARK_NOTIFICATION_READ
        )
        markReadIntent.putExtra(MiscReceiver.MESSAGE_ID, notificationId)
        markReadIntent.putExtra(MiscReceiver.NOTIFICATION_ID, mLastNotificationId)

        val markReadPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                App.instance,
                mActionId,
                markReadIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                App.instance,
                mActionId,
                markReadIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        mActionId++

        val deleteIntent = Intent(App.instance, MiscReceiver::class.java)
        deleteIntent.putExtra(MiscReceiver.EXTRA_ACTION, MiscReceiver.ACTION_DELETE_NOTIFICATION)
        deleteIntent.putExtra(MiscReceiver.MESSAGE_ID, notificationId)
        deleteIntent.putExtra(MiscReceiver.NOTIFICATION_ID, mLastNotificationId)

        val deleteNotificationPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                App.instance,
                mActionId,
                deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                App.instance,
                mActionId,
                deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        mActionId++
        val notificationIntent = Intent(App.instance, MessagesActivity::class.java)
        val contentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                App.instance,
                mActionId, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                App.instance,
                mActionId, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
        mActionId++
        val newNotification =
            NotificationCompat.Builder(App.instance, ALERT_RECEIVED_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_notification_mono)
                .setContentTitle("Изменился параметр")
                .setAutoCancel(true)
                .addAction(
                    R.drawable.ic_baseline_delete_24,
                    App.instance.getString(R.string.delete_message),
                    deleteNotificationPendingIntent
                )
                .addAction(
                    R.drawable.ic_baseline_mark_email_read_24,
                    App.instance.getString(R.string.mark_as_read_titile),
                    markReadPendingIntent
                )
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setLights(Color.YELLOW, 500, 500)
                .setChannelId(ALERT_RECEIVED_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(if (s == "1") "Теперь вы будуте получать уведомления о потраченной электроэнергии" else "Уведомления о потраченной электроэнергии отключены")
                )
                .build()
        mNotificationManager!!.notify(mLastNotificationId, newNotification)
        mLastNotificationId++
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun notifyUsedData(
        spend: String?,
        spendForMonth: String?,
        monthCost: String?,
        currentTimestamp: String?,
        previousTimestamp: String?,
        periodCost: String?,
        notificationId: String?
    ) {
        // add actions for buttons on notification
        val markReadIntent = Intent(App.instance, MiscReceiver::class.java)
        markReadIntent.putExtra(
            MiscReceiver.EXTRA_ACTION,
            MiscReceiver.ACTION_MARK_NOTIFICATION_READ
        )
        markReadIntent.putExtra(MiscReceiver.MESSAGE_ID, notificationId)
        markReadIntent.putExtra(MiscReceiver.NOTIFICATION_ID, mLastNotificationId)

        val markReadPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                App.instance,
                mActionId,
                markReadIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                App.instance,
                mActionId,
                markReadIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        mActionId++

        val deleteIntent = Intent(App.instance, MiscReceiver::class.java)
        deleteIntent.putExtra(MiscReceiver.EXTRA_ACTION, MiscReceiver.ACTION_DELETE_NOTIFICATION)
        deleteIntent.putExtra(MiscReceiver.MESSAGE_ID, notificationId)
        deleteIntent.putExtra(MiscReceiver.NOTIFICATION_ID, mLastNotificationId)

        val deleteNotificationPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(
                App.instance,
                mActionId,
                deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getBroadcast(
                App.instance,
                mActionId,
                deleteIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
        mActionId++

        val text = String.format(
            Locale.ENGLISH,
            App.instance.getString(R.string.spended_electicity_message),
            TimeHandler().timestampToDatetime(previousTimestamp!!.toInt()),
            TimeHandler().timestampToDatetime(currentTimestamp!!.toInt()),
            spend,
            periodCost,
            spendForMonth,
            monthCost
        )
        val notificationIntent = Intent(App.instance, ContentActivity::class.java)
        notificationIntent.putExtra("type", "power_data")
        notificationIntent.putExtra("notificationText", text)
        notificationIntent.putExtra("id", notificationId)
        val contentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                App.instance,
                mActionId, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                App.instance,
                mActionId, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
        mActionId++
//        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(App.instance).run {
//            // Add the intent, which inflates the back stack
//            addNextIntent(notificationIntent)
//            // Get the PendingIntent containing the entire back stack
//            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
//        }
        val newNotification =
            NotificationCompat.Builder(App.instance, ALERT_RECEIVED_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_notification_mono)
                .setContentTitle("Потребление электроэнергии")
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(contentIntent)
                .setLights(Color.YELLOW, 500, 500)
                .setChannelId(ALERT_RECEIVED_CHANNEL_ID)
                .addAction(
                    R.drawable.ic_baseline_delete_24,
                    App.instance.getString(R.string.delete_message),
                    deleteNotificationPendingIntent
                )
                .addAction(
                    R.drawable.ic_baseline_mark_email_read_24,
                    App.instance.getString(R.string.mark_as_read_titile),
                    markReadPendingIntent
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(text)
                )
                .build()
        mNotificationManager!!.notify(mLastNotificationId, newNotification)
        mLastNotificationId++
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showContentNotification(data: MutableMap<String, String>) {
        val notificationIntent = Intent(App.instance, MessagesActivity::class.java)
        notificationIntent.putExtra("type", data["notification"])
        notificationIntent.putExtra("billId", data["bill_id"])
        notificationIntent.putExtra("id", data["notification_id"])
        notificationIntent.putExtra("title", data["title"])
        notificationIntent.putExtra("text", data["text"])
        val contentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                App.instance,
                mActionId, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                App.instance,
                mActionId, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
        mActionId++
        val newNotificationBuilder =
            NotificationCompat.Builder(mContext, ALERT_RECEIVED_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_notification_mono)
                .setContentTitle(data["title"])
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(contentIntent)
                .setLights(Color.YELLOW, 500, 500)
                .setChannelId(ALERT_RECEIVED_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(data["text"])
                )
        if(data["notification_id"] != null){
            // add actions for buttons on notification
            val markReadIntent = Intent(App.instance, MiscReceiver::class.java)
            markReadIntent.putExtra(
                MiscReceiver.EXTRA_ACTION,
                MiscReceiver.ACTION_MARK_NOTIFICATION_READ
            )
            markReadIntent.putExtra(MiscReceiver.MESSAGE_ID, data["notification_id"])
            markReadIntent.putExtra(MiscReceiver.NOTIFICATION_ID, mLastNotificationId)

            val markReadPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(
                    App.instance,
                    mActionId,
                    markReadIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    App.instance,
                    mActionId,
                    markReadIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            mActionId++

            val deleteIntent = Intent(App.instance, MiscReceiver::class.java)
            deleteIntent.putExtra(
                MiscReceiver.EXTRA_ACTION,
                MiscReceiver.ACTION_DELETE_NOTIFICATION
            )
            deleteIntent.putExtra(MiscReceiver.MESSAGE_ID, data["notification_id"])
            deleteIntent.putExtra(MiscReceiver.NOTIFICATION_ID, mLastNotificationId)

            val deleteNotificationPendingIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(
                        App.instance,
                        mActionId,
                        deleteIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    )
                } else {
                    PendingIntent.getBroadcast(
                        App.instance,
                        mActionId,
                        deleteIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            mActionId++
            newNotificationBuilder
                .addAction(
                    R.drawable.ic_baseline_delete_24,
                    App.instance.getString(R.string.delete_message),
                    deleteNotificationPendingIntent
                )
                .addAction(
                    R.drawable.ic_baseline_mark_email_read_24,
                    App.instance.getString(R.string.mark_as_read_titile),
                    markReadPendingIntent
                )
        }
        val newNotification = newNotificationBuilder.build()
        mNotificationManager!!.notify(mLastNotificationId, newNotification)
        mLastNotificationId++
    }
    @SuppressLint("UnspecifiedImmutableFlag")
    fun showBroadcastNotification(data: MutableMap<String, String>) {
        val notificationIntent = Intent(App.instance, MessagesActivity::class.java)
        notificationIntent.putExtra("type", data["notification"])
        notificationIntent.putExtra("id", data["broadcast_id"])
        notificationIntent.putExtra("title", data["title"])
        notificationIntent.putExtra("text", data["text"])
        val contentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                App.instance,
                mActionId, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                App.instance,
                mActionId, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )
        }
        mActionId++
        val newNotificationBuilder =
            NotificationCompat.Builder(mContext, ALERT_RECEIVED_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_notification_mono)
                .setContentTitle(data["title"])
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(contentIntent)
                .setLights(Color.YELLOW, 500, 500)
                .setChannelId(ALERT_RECEIVED_CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(data["text"])
                )
        if(data["broadcast_id"] != null){
            // add actions for buttons on notification
            val markReadIntent = Intent(App.instance, MiscReceiver::class.java)
            markReadIntent.putExtra(
                MiscReceiver.EXTRA_ACTION,
                MiscReceiver.ACTION_MARK_BROADCAST_READ
            )
            markReadIntent.putExtra(MiscReceiver.MESSAGE_ID, data["broadcast_id"])
            markReadIntent.putExtra(MiscReceiver.NOTIFICATION_ID, mLastNotificationId)

            val markReadPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(
                    App.instance,
                    mActionId,
                    markReadIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    App.instance,
                    mActionId,
                    markReadIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            mActionId++

            val deleteIntent = Intent(App.instance, MiscReceiver::class.java)
            deleteIntent.putExtra(
                MiscReceiver.EXTRA_ACTION,
                MiscReceiver.ACTION_DELETE_BROADCAST
            )
            deleteIntent.putExtra(MiscReceiver.MESSAGE_ID, data["broadcast_id"])
            deleteIntent.putExtra(MiscReceiver.NOTIFICATION_ID, mLastNotificationId)

            val deleteNotificationPendingIntent =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(
                        App.instance,
                        mActionId,
                        deleteIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                    )
                } else {
                    PendingIntent.getBroadcast(
                        App.instance,
                        mActionId,
                        deleteIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
            mActionId++
            newNotificationBuilder
                .addAction(
                    R.drawable.ic_baseline_delete_24,
                    App.instance.getString(R.string.delete_message),
                    deleteNotificationPendingIntent
                )
                .addAction(
                    R.drawable.ic_baseline_mark_email_read_24,
                    App.instance.getString(R.string.mark_as_read_titile),
                    markReadPendingIntent
                )
        }
        val newNotification = newNotificationBuilder.build()
        mNotificationManager!!.notify(mLastNotificationId, newNotification)
        mLastNotificationId++
    }

    companion object {
        private const val CHECKER_CHANNEL_ID = "checker"
        private const val DEFENCE_STATE_CHANGED_CHANNEL_ID = "congrats"
        private const val ALERT_RECEIVED_CHANNEL_ID = "alert received"
        const val FILE_LOAD_CHANNEL_ID = "file load"
        const val FILE_LOADED_CHANNEL_ID = "file loaded"
    }

    init {
        mNotificationManager =
            mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        // создам каналы уведомлений
        createChannels()
    }
}