package net.veldor.oblepiha_kotlin.model.data_source

import android.util.Log
import androidx.paging.PositionalDataSource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.model.selections.GetMessagesResponse
import net.veldor.oblepiha_kotlin.model.selections.MessageItem
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import net.veldor.oblepiha_kotlin.model.view_models.MessageViewModel

internal class GetMessagesListDataSource :
    PositionalDataSource<MessageItem?>() {
    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<MessageItem?>
    ) {
        GlobalScope.launch{
            // request
            val connector = MyConnector()
            val response =
                connector.requestMessagesList(params.requestedStartPosition, params.requestedLoadSize)
            if (response.isNotEmpty()) {
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                val result: GetMessagesResponse = gson.fromJson(
                    response,
                    GetMessagesResponse::class.java
                )
                result.modify()
                MessageViewModel.liveDataReceived.postValue(result.list.size)
                callback.onResult(result.list, 0, result.count)
            } else {
                callback.onResult(listOf(), 0)
            }
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<MessageItem?>) {
        // request
        val connector = MyConnector()
        val response = connector.requestMessagesList(params.startPosition, params.loadSize)
        if (response.isNotEmpty()) {
            val builder = GsonBuilder()
            val gson: Gson = builder.create()
            val result: GetMessagesResponse = gson.fromJson(
                response,
                GetMessagesResponse::class.java
            )
            result.modify()
            callback.onResult(result.list)
        } else {
            callback.onResult(listOf())
        }
    }
}