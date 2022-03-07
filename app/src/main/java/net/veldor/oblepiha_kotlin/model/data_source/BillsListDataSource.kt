package net.veldor.oblepiha_kotlin.model.data_source

import android.util.Log
import androidx.paging.PositionalDataSource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.model.selections.*
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import net.veldor.oblepiha_kotlin.model.view_models.BillsListViewModel

internal class BillsListDataSource :
    PositionalDataSource<BillListItem?>() {
    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<BillListItem?>
    ) {
        GlobalScope.launch{
            // request
            val connector = MyConnector()
            val response =
                connector.requestBillsList(params.requestedStartPosition, params.requestedLoadSize)
            if (response.isNotEmpty()) {
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                val result: GetBillsDataResponse = gson.fromJson(
                    response,
                    GetBillsDataResponse::class.java
                )
                result.modify()
                BillsListViewModel.isLoaded.postValue(result.list.isEmpty())
                callback.onResult(result.list, 0, result.count)
            } else {
                callback.onResult(listOf(), 0)
            }
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<BillListItem?>) {
        // request
        val connector = MyConnector()
        val response = connector.requestBillsList(params.startPosition, params.loadSize)
        if (response.isNotEmpty()) {
            Log.d("surprise", "BillsListDataSource.kt 25: $response")
            val builder = GsonBuilder()
            val gson: Gson = builder.create()
            val result: GetBillsDataResponse = gson.fromJson(
                response,
                GetBillsDataResponse::class.java
            )
            result.modify()
            callback.onResult(result.list)
        } else {
            callback.onResult(listOf())
        }
    }
}