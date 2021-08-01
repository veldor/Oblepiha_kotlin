package net.veldor.oblepiha_kotlin.model.utils

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.MutableLiveData
import net.veldor.oblepiha_kotlin.App
import java.io.File

class FileHandler {
    fun fileExists(fileName: String): Boolean {
        val newFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        return newFile.isFile
    }

    fun shareFile(fileName: String) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        val uri = FileProvider.getUriForFile(
            App.instance,
            App.instance.packageName.toString() + ".provider",
            file
        )
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        if (fileName!!.endsWith("zip")) {
            shareIntent.type = "application/zip"
        } else {
            shareIntent.type = "application/pdf"
        }
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        if (intentCanBeHandled(shareIntent)) {
            App.instance.startActivity(shareIntent)
        } else {
            Toast.makeText(
                App.instance,
                "Не найдены приложения, с которыми можно поделиться этим файлом",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun openFIle(fileName: String) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        val uri = FileProvider.getUriForFile(
            App.instance,
            App.instance.packageName.toString() + ".provider",
            file
        )
        val mimeType =
            if (fileName.endsWith("zip")) "application/zip" else "application/pdf"
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, mimeType)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        if (intentCanBeHandled(intent)) {
            App.instance.startActivity(intent)
        } else {
            Toast.makeText(
                App.instance,
                "Не найдено приложение, открывающее данный файл",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun clearFile(fileName: String) {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        file.delete()
    }

    companion object {
        fun intentCanBeHandled(intent: Intent): Boolean {
            val packageManager: PackageManager = App.instance.packageManager
            return intent.resolveActivity(packageManager) != null
        }

        var mDownloadProgress = MutableLiveData<HashMap<String, Int>>()
    }
}