package net.veldor.oblepiha_kotlin.model.utils

import android.os.Build
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.selections.*
import net.veldor.oblepiha_kotlin.model.view_models.BillDetailsViewModel
import net.veldor.oblepiha_kotlin.model.view_models.BillsListViewModel
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection

class MyConnector {
    fun requestLogin(login: String, password: String, token: String?): String {
        val request = LoginRequest()
        request.login = login
        request.password = password
        request.token = token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        Log.d("surprise", "Connector requestLogin 38: login $requestBody")
        return request(requestBody)
    }

    fun requestCurrentStatus(): String {
        val request = CurrentStatusRequest()
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    fun setAlertMode(mode: Boolean): String {
        val request = ChangeAlertModeRequest()
        request.token = App.instance.preferences.token
        request.mode = mode
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    private fun request(requestBody: String): String {
        var responseText = ""
        try {
            val url = URL(API_ADDRESS)
            val conn: HttpURLConnection = url.openConnection() as HttpsURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.doInput = true
            val localDataOutputStream = DataOutputStream(conn.outputStream)
            localDataOutputStream.writeBytes(requestBody)
            localDataOutputStream.flush()
            localDataOutputStream.close()
            if (conn.responseCode == 200) {
                val br = BufferedReader(InputStreamReader(conn.inputStream, "UTF-8"))
                val sb = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    sb.append(line).append("\n")
                }
                br.close()
                responseText = sb.toString()
            } else {
                Log.d("surprise", "Connector request 60: error with status " + conn.responseCode)
                val br = BufferedReader(InputStreamReader(conn.errorStream, "UTF-8"))
                val sb = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    sb.append(line).append("\n")
                }
                br.close()
                Log.d("surprise", "MyConnector.kt 77: $sb")
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.d("surprise", "Connector request 30: we have problem " + e.message)
            App.instance.connectionError.postValue(true)
            e.printStackTrace()
        }
        return responseText
    }

    @Throws(java.lang.Exception::class)
    private fun getFileConnection(): HttpURLConnection {
        val url = URL(FILE_API_ADDRESS)
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "POST"
        con.setRequestProperty("Content-Type", "application/json; utf-8")
        con.setRequestProperty("Accept", "application/json")
        con.doOutput = true
        return con
    }
    fun getInvoiceRequest(id: String) {
        val con: HttpURLConnection = getFileConnection()
        val request = BillInvoiceRequest()
        request.id = id
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        Log.d("surprise", "getInvoiceRequest: $requestBody")
        val os = con.outputStream
        val input: ByteArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            requestBody.toByteArray(StandardCharsets.UTF_8)
        } else {
            requestBody.toByteArray(charset("utf-8"))
        }
        os.write(input, 0, input.size)
        Log.d("surprise", "getInvoiceRequest: ${con.responseCode}")
        if (con.responseCode == 200 && con.contentLength > 0) {
            val newFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                id + ".pdf"
            )
            // сохраню данные в файл
            FileOutputStream(newFile).use { out ->
                val buffer = ByteArray(1024)
                val contentLength: Int = con.contentLength
                var len: Int
                val inputStream: InputStream = con.inputStream
                while (inputStream.read(buffer).also { len = it } != -1) {
                    out.write(buffer, 0, len)
                }
                inputStream.close()
                Log.d("surprise", "getInvoiceRequest: load finished")
                BillDetailsViewModel.someFileLoadLiveData.postValue(true)
            }
        }
        else if(con.responseCode == 200){
            val inputAsString = con.inputStream.bufferedReader().use { it.readText() }
            Log.d("surprise", "getInvoiceRequest: $inputAsString")
        }
        else if(con.responseCode == 500){
            val inputAsString = con.errorStream.bufferedReader().use { it.readText() }
            Log.d("surprise", "getInvoiceRequest: $inputAsString")
        }
        else{
            Log.d("surprise", "getInvoiceRequest: no answer")
        }
    }

    fun checkModeChanged(): Boolean {
        val request = IsAlertModeChangedRequest()
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        val answer: String = request(requestBody)
        if (answer.isNotEmpty()) {
            // если запрос успешно завершён- верну true
            val builder = GsonBuilder()
            val responseGson = builder.create()
            val response: ApiCurrentStatusResponse = responseGson.fromJson(
                answer,
                ApiCurrentStatusResponse::class.java
            )
            return response.status == "success"
        }
        return false
    }

    fun setAlertsHandled(): Boolean {
        val request = AlertsHandledRequest()
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        val answer: String = request(requestBody)
        if (answer.isNotEmpty()) {
            // если запрос успешно завершён- верну true
            val builder = GsonBuilder()
            val responseGson = builder.create()
            val response: ApiCurrentStatusResponse = responseGson.fromJson(
                answer,
                ApiCurrentStatusResponse::class.java
            )
            return response.status.equals("success")
        }
        return false
    }

    fun requestLogout(token: String?, firebase_token: String?): String {
        val request = LogoutRequest()
        request.token = token
        request.firebase_token = firebase_token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    fun requestAccrualsStatus(): String {
        val request = AccrualsStatusRequest()
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    fun requestPowerList(requestedStartPosition: Int, requestedLoadSize: Int): String {
        val request = GetPowerDataRequest()
        request.limit = requestedLoadSize
        request.offset = requestedStartPosition
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    fun requestMembershipList(requestedStartPosition: Int, requestedLoadSize: Int): String {
        val request = GetMembershipDataRequest()
        request.limit = requestedLoadSize
        request.offset = requestedStartPosition
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    fun requestTargetList(requestedStartPosition: Int, requestedLoadSize: Int): String {
        val request = GetTargetDataRequest()
        request.limit = requestedLoadSize
        request.offset = requestedStartPosition
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    fun requestBillState(): String {
        val request = BillStateRequest()
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    fun requestBillsList(requestedStartPosition: Int, requestedLoadSize: Int): String {
        val request = GetBillsDataRequest()
        request.limit = requestedLoadSize
        request.offset = requestedStartPosition
        request.isPayed = BillsListViewModel.isClosed
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    fun requestBillInfo(id: Int): String {
        val request = GetBillInfoRequest()
        request.id = id
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    fun requestPays(period: String, type: String): String {
        val request = EntityPaysRequest()
        request.period = period
        Log.d("surprise", "MyConnector.kt 204: $period")
        request.type = type
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    fun requestBillQR(billId: String): String {
        val request = BillQRRequest()
        request.id = billId
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    fun requestSuburban(): String {
        val request = SuburbanRequest()
        request.token = App.instance.preferences.token
        val gson = Gson()
        val requestBody = gson.toJson(request)
        return request(requestBody)
    }

    fun requestBillInvoice(id: String) {
        val outputDir: File =
            App.instance.cacheDir // context being the Activity pointer
        val outputFile: File = File.createTempFile(id + ".pdf", "", outputDir)
    }

    companion object {
        const val STATUS_SUCCESS = "success"
        private const val API_ADDRESS = "https://oblepiha.site/api"
        private const val FILE_API_ADDRESS = "https://oblepiha.site/file-api"
    }
}