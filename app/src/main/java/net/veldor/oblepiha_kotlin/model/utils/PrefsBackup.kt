package net.veldor.oblepiha_kotlin.model.utils

import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import net.veldor.oblepiha_kotlin.App
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class PrefsBackup {

    companion object {

        const val PREF_BACKUP_NAME = "pref_backup"
        const val DB_BACKUP_NAME = "db_backup"
        const val DB_BACKUP_NAME1 = "db_backup_shm"
        const val DB_BACKUP_NAME2 = "db_backup_wal"
        const val BUFFER = 1024
    }

    fun doBackup(dir: DocumentFile) {
        // do backup

        // сохраню файл в выбранную директорию
        val sdf = SimpleDateFormat("yyyy/MM/dd HH-mm-ss", Locale.ENGLISH)
        val filename = "Настройки " + sdf.format(Date())
        val backupFile =
            dir.createFile(
                "application/zip",
                filename
            )
        val stream = App.instance.contentResolver.openOutputStream(backupFile!!.uri)
        val out = ZipOutputStream(BufferedOutputStream(stream))
        val dataBuffer = ByteArray(BUFFER)
        val sharedPrefsFile: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File(
                App.instance.dataDir
                    .toString() + "/shared_prefs/net.veldor.oblepiha_kotlin_preferences.xml"
            )
        } else {
            File(
                Environment.getDataDirectory()
                    .toString() + "/shared_prefs/net.veldor.oblepiha_kotlin_preferences.xml"
            )
        }
        writeToZip(
            out,
            dataBuffer,
            sharedPrefsFile,
            PREF_BACKUP_NAME
        )

        // backup DB
        App.instance.database.close()
        val dbfile: File = App.instance.getDatabasePath("database")

        writeToZip(
            out,
            dataBuffer,
            dbfile,
            DB_BACKUP_NAME
        )
        val dbFile1: File = App.instance.getDatabasePath("database-shm")

        writeToZip(
            out,
            dataBuffer,
            dbFile1,
            DB_BACKUP_NAME1
        )
        val dbFile2: File = App.instance.getDatabasePath("database-wal")

        writeToZip(
            out,
            dataBuffer,
            dbFile2,
            DB_BACKUP_NAME2
        )
        out.close()
        // share file when it ready
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val uri = backupFile.uri
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "application/zip"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            App.instance.startActivity(shareIntent)
        }
    }

    fun restoreBackup(file: DocumentFile) {
        //
        if (file.isFile) {
            val stream = App.instance.contentResolver.openInputStream(file.uri)
            val zin = ZipInputStream(stream)
            var ze: ZipEntry? = zin.nextEntry
            App.instance.database.close()
            while (ze != null) {
                when (ze.name) {
                    PREF_BACKUP_NAME -> {
                        // restore preferences
                        val sharedPrefsFile: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            File(
                                App.instance.dataDir
                                    .toString() + "/shared_prefs/net.velor.oblepiha_kotlin_preferences.xml"
                            )
                        } else {
                            File(
                                Environment.getDataDirectory()
                                    .toString() + "/shared_prefs/net.velor.oblepiha_kotlin_preferences.xml"
                            )
                        }
                        extractFromZip(zin, sharedPrefsFile)
                    }
                    DB_BACKUP_NAME -> {
                        val dbFIle: File = App.instance.getDatabasePath("database")
                        extractFromZip(zin, dbFIle)
                    }
                    DB_BACKUP_NAME1 -> {
                        val dbFile: File = App.instance.getDatabasePath("database-shm")
                        extractFromZip(zin, dbFile)
                    }
                    DB_BACKUP_NAME2 -> {
                        val dbFile: File = App.instance.getDatabasePath("database-wal")
                        extractFromZip(zin, dbFile)
                    }
                }
                ze = zin.nextEntry
            }
//            while (zin.nextEntry.also { ze = it } != null) {
//                Log.d("surprise", "doWork: found file " + ze.name)
//            }
        }

    }

    private fun writeToZip(
        stream: ZipOutputStream,
        dataBuffer: ByteArray,
        oldFileName: File,
        newFileName: String
    ) {
        if (oldFileName.exists()) {
            val fis: FileInputStream
            try {
                fis = FileInputStream(oldFileName)
                val origin = BufferedInputStream(
                    fis,
                    BUFFER
                )
                val entry = ZipEntry(newFileName)
                stream.putNextEntry(entry)
                var count: Int
                while (origin.read(
                        dataBuffer,
                        0,
                        BUFFER
                    ).also {
                        count = it
                    } != -1
                ) {
                    stream.write(dataBuffer, 0, count)
                }
                origin.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun extractFromZip(zis: ZipInputStream, fileName: File) {
        try {
            val fout = FileOutputStream(fileName)
            var c = zis.read()
            while (c != -1) {
                fout.write(c)
                c = zis.read()
            }
            zis.closeEntry()
            fout.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}