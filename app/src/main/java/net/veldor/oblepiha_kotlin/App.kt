package net.veldor.oblepiha_kotlin

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import androidx.work.*
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import net.veldor.oblepiha_kotlin.model.database.AppDatabase
import net.veldor.oblepiha_kotlin.model.selections.ApiCurrentStatusResponse
import net.veldor.oblepiha_kotlin.model.utils.MyFirebaseHandler
import net.veldor.oblepiha_kotlin.model.utils.MyNotify
import net.veldor.oblepiha_kotlin.model.utils.MyPreferences
import net.veldor.oblepiha_kotlin.model.workers.CheckStatusWorker
import net.veldor.oblepiha_kotlin.view.AlarmActivity
import java.util.concurrent.TimeUnit


class App : Application() {
    // хранилище статуса HTTP запроса
    val RequestStatus = MutableLiveData<String>()

    var mCurrentStatusResponse: MutableLiveData<ApiCurrentStatusResponse> =
        MutableLiveData<ApiCurrentStatusResponse>()

    val connectionError = MutableLiveData<Boolean?>()

    var isAlertWindowShowed = false

    lateinit var notifier: MyNotify
    lateinit var preferences: MyPreferences

    private lateinit var mDatabase: AppDatabase

    override fun onCreate() {
        super.onCreate()
        Log.d("surprise", "App.kt 40: i start")
        FirebaseApp.initializeApp(this)
        instance = this
        startMainWorker()
        notifier = MyNotify()
        preferences = MyPreferences()
        if (preferences.firebaseToken == null) {
            MyFirebaseHandler().token
        }
        // получаю базу данных
        mDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "database"
        )
            .allowMainThreadQueries()
            .build()
    }

    val database: AppDatabase
        get() = mDatabase

    @SuppressLint("WrongConstant")
    fun showAlertWindow(rawData: String?) {
        // разбе
        Log.d("surprise", "App showAlertWindow 41: show window")
        val alarmIntent = Intent(this, AlarmActivity::class.java)
        alarmIntent.putExtra(AlarmActivity.RAW_DATA_EXTRA, rawData)
        alarmIntent.setClass(this, AlarmActivity::class.java)
        alarmIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(alarmIntent)
    }

    private fun startMainWorker() {
        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        // запущу рабочего, который периодически будет обновлять данные
        val periodicTask: PeriodicWorkRequest =
            PeriodicWorkRequest.Builder(CheckStatusWorker::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "periodic check",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicTask
        )
    }

    companion object {
        lateinit var instance: App
            private set
    }
}