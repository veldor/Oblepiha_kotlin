package net.veldor.oblepiha_kotlin.model.view_models

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.model.selections.MessageItem
import net.veldor.oblepiha_kotlin.model.utils.MyConnector

class MessageViewModel : ViewModel() {
    fun markAsRead(item: MessageItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val connector = MyConnector()
            val response = connector.requestNotificationRead(item.id)
            if (response.isEmpty()) {
                Toast.makeText(
                    App.instance,
                    App.instance.getString(R.string.wrong_request_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun markBroadcastAsRead(broadcast_id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val connector = MyConnector()
            val response = connector.requestBroadcastRead(broadcast_id)
            if (response.isEmpty()) {
                Toast.makeText(
                    App.instance,
                    App.instance.getString(R.string.wrong_request_message),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun delete(item: MessageItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val connector = MyConnector()
            val response = connector.requestDeleteNotification(item.id)
            if (response.isEmpty()) {
                Toast.makeText(
                    App.instance,
                    App.instance.getString(R.string.wrong_request_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                liveRequireUpdateList.postValue(true)
            }
        }
    }

    fun deleteBroadcast(broadcast_id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val connector = MyConnector()
            val response = connector.requestDeleteBroadcast(broadcast_id)
            if (response.isEmpty()) {
                Toast.makeText(
                    App.instance,
                    App.instance.getString(R.string.wrong_request_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                liveRequireUpdateList.postValue(true)
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch(Dispatchers.IO) {
            val connector = MyConnector()
            val response = connector.requestMarkAllNotificationsRead()
            if (response.isEmpty()) {
                Toast.makeText(
                    App.instance,
                    App.instance.getString(R.string.wrong_request_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                liveRequireUpdateList.postValue(true)
            }
        }
    }

    fun markAsRead(id: String?) {
        if (id != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val connector = MyConnector()
                val response = connector.requestNotificationRead(id.toInt())
                if (response.isEmpty()) {
                    Toast.makeText(
                        App.instance,
                        App.instance.getString(R.string.wrong_request_message),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun delete(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val connector = MyConnector()
            val response = connector.requestDeleteNotification(id.toInt())
            if (response.isEmpty()) {
                Toast.makeText(
                    App.instance,
                    App.instance.getString(R.string.wrong_request_message),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                liveRequireUpdateList.postValue(true)
            }
        }
    }

    companion object {
        val liveDataReceived: MutableLiveData<Int> = MutableLiveData()
        val liveRequireUpdateList: MutableLiveData<Boolean> = MutableLiveData()
    }
}