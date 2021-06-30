package net.veldor.oblepiha_kotlin.model.view_models

import androidx.lifecycle.ViewModel
import androidx.work.*
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.workers.SetAlertsAcceptedWorker

class AlertViewModel : ViewModel() {
    fun sendAlertsAccepted() {
        // запущу рабочего, который отметит все тревоги на данный момент просмотренными
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val checkStatusWork = OneTimeWorkRequest.Builder(
            SetAlertsAcceptedWorker::class.java
        ).addTag(SetAlertsAcceptedWorker.ACTION).setConstraints(constraints).build()
        WorkManager.getInstance(App.instance).enqueueUniqueWork(
            SetAlertsAcceptedWorker.ACTION,
            ExistingWorkPolicy.REPLACE,
            checkStatusWork
        )
    }
}