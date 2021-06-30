package net.veldor.oblepiha_kotlin.model.view_models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.selections.ApiLoginResponse
import net.veldor.oblepiha_kotlin.model.utils.MyConnector

class LoginViewModel : ViewModel() {
    fun login(login: String, password: String) {
        viewModelScope.launch (Dispatchers.IO ){
            val token: String? = App.instance.preferences.firebaseToken
            val connector = MyConnector()
            val responseText: String = connector.requestLogin(login, password, token)
            Log.d("surprise", "LoginViewModel.kt 20: $responseText")
            // разберу ответ
            val builder = GsonBuilder()
            val gson = builder.create()
            val response: ApiLoginResponse =
                gson.fromJson(responseText, ApiLoginResponse::class.java)
            loginResult.postValue(response)
            if(response.status == "success" && response.token != null && response.token!!.isNotEmpty()){
                App.instance.preferences.saveToken(response.token)
            }

        }
    }
    val loginResult: MutableLiveData<ApiLoginResponse> = MutableLiveData()
}