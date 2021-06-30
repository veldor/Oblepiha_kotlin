package net.veldor.oblepiha_kotlin.model.utils

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.selections.*
import net.veldor.oblepiha_kotlin.view.fragments.AccrualsFragment
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
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
            e.printStackTrace()
        }
        return responseText
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
        Log.d(
            "surprise",
            "Connector setAlertsHandled 115: send accept alert $requestBody"
        )
        val answer: String = request(requestBody)
        Log.d("surprise", "Connector setAlertsHandled 117: answer is $answer")
        if (answer.isNotEmpty()) {
            Log.d("surprise", "Connector alerts handled 118: answer is $answer")
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

    companion object {
        const val STATUS_SUCCESS = "success"
        private const val API_ADDRESS = "https://oblepiha.site/api"
    }
}