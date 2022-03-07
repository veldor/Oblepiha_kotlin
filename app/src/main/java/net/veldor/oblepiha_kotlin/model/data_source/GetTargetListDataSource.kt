package net.veldor.oblepiha_kotlin.model.data_source

import android.util.Log
import androidx.paging.PositionalDataSource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.model.selections.*
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsMembershipViewModel
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsTargetViewModel

internal class GetTargetListDataSource :
    PositionalDataSource<TargetListItem?>() {
    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<TargetListItem?>
    ) {
        GlobalScope.launch{
            // request
            val connector = MyConnector()
            val response =
                connector.requestTargetList(params.requestedStartPosition, params.requestedLoadSize)
            Log.d("surprise", "GetMembershipListDataSource.kt 25: $response")
            if (response.isNotEmpty()) {
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                val result: GetTargetDataResponse = gson.fromJson(
                    response,
                    GetTargetDataResponse::class.java
                )
                result.modify()
                AccrualsTargetViewModel.isLoaded.postValue(result.list.isEmpty())
                callback.onResult(result.list, 0, result.count)
            } else {
                callback.onResult(listOf(), 0)
            }
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<TargetListItem?>) {
        // request
        val connector = MyConnector()
        val response = connector.requestTargetList(params.startPosition, params.loadSize)
        if (response.isNotEmpty()) {
            val builder = GsonBuilder()
            val gson: Gson = builder.create()
            val result: GetTargetDataResponse = gson.fromJson(
                response,
                GetTargetDataResponse::class.java
            )
            result.modify()
            callback.onResult(result.list)
        } else {
            callback.onResult(listOf())
        }
    }
}