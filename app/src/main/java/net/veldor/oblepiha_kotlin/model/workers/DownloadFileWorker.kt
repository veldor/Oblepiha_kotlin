package net.veldor.oblepiha_kotlin.model.workers

import android.app.Notification
import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.utils.FileHandler
import java.io.File
import java.util.*


class DownloadFileWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    private var mNotificationId = 0

    override fun doWork(): Result {
//        val info: ForegroundInfo = createForegroundInfo()
//        setForegroundAsync(info)
//        // load file
//        val fileName: String = getDataItem(PROP_FILE_NAME)!!
//        val visibleName: String = getDataItem(PROP_VISIBLE_NAME)!! + "." + fileName.split(".").last()
//        val tempName: String = System.currentTimeMillis().toString()
//        // create a temp file for load
//        val outputDir: File =
//            App.instance.cacheDir // context being the Activity pointer
//        val outputFile: File = File.createTempFile(tempName, "", outputDir)
//        val conn = Connection()
//        conn.addArgument("file_name", fileName)
//        conn.handleFileRequest("get_file", outputFile, mNotificationId, fileName, visibleName)
//        // save file to download folder
//        val newFile = File(
//            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
//            visibleName
//        )
//        outputFile.copyTo(newFile, true)
//        outputFile.delete()
//        Log.d("surprise", "doWork: file loaded!")
//        // update progress
//        val currentValue = FileHandler.mDownloadProgress.value
//        // found file value and delete it if exists
//        if(currentValue != null){
//            currentValue.remove(fileName)
//            FileHandler.mDownloadProgress.postValue(currentValue)
//        }
//        Handler(Looper.getMainLooper()).post {
//            Toast.makeText(App.instance, String.format(Locale.ENGLISH, "%s loaded to downloads folder", visibleName), Toast.LENGTH_LONG).show()
//            //App.instance.notifier.notifyFileLoaded(newFile)
//        }
        return Result.success()
    }

    companion object {
        const val PROP_FILE_NAME = "file name"
        const val PROP_VISIBLE_NAME = "visible file name"
    }

    private fun createForegroundInfo(): ForegroundInfo {
        // Build a notification
        val notification: Notification = App.instance.notifier.createFileLoadNotification()
        mNotificationId = App.instance.notifier.getNextIdentifier()
        return ForegroundInfo(mNotificationId, notification)
    }

    protected fun getDataItem(itemName: String?): String? {
        val data = inputData
        return data.getString(itemName!!)
    }
}