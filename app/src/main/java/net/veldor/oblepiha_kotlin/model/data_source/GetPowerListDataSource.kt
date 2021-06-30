package net.veldor.oblepiha_kotlin.model.data_source

import android.util.Log
import androidx.paging.PositionalDataSource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.model.selections.GetPowerDataResponse
import net.veldor.oblepiha_kotlin.model.selections.PowerListItem
import net.veldor.oblepiha_kotlin.model.utils.MyConnector

internal class GetPowerListDataSource :
    PositionalDataSource<PowerListItem?>() {
    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<PowerListItem?>
    ) {
        GlobalScope.launch{
            // request
            val connector = MyConnector()
            val response =
                connector.requestPowerList(params.requestedStartPosition, params.requestedLoadSize)
            if (response.isNotEmpty()) {
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                val result: GetPowerDataResponse = gson.fromJson(
                    response,
                    GetPowerDataResponse::class.java
                )
                result.modify()
                callback.onResult(result.list, 0, result.count)
            } else {
                callback.onResult(listOf(), 0)
            }
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<PowerListItem?>) {
        // request
        val connector = MyConnector()
        val response = connector.requestPowerList(params.startPosition, params.loadSize)
        Log.d("surprise", "GetPowerListDataSource.kt 37: $response")
        if (response.isNotEmpty()) {
            val builder = GsonBuilder()
            val gson: Gson = builder.create()
            val result: GetPowerDataResponse = gson.fromJson(
                response,
                GetPowerDataResponse::class.java
            )
            result.modify()
            callback.onResult(result.list)
        } else {
            callback.onResult(listOf())
        }
    }
}