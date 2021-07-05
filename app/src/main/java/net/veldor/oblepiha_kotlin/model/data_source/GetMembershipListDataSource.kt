package net.veldor.oblepiha_kotlin.model.data_source

import android.util.Log
import androidx.paging.PositionalDataSource
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.veldor.oblepiha_kotlin.model.selections.GetMembershipDataResponse
import net.veldor.oblepiha_kotlin.model.selections.MembershipListItem
import net.veldor.oblepiha_kotlin.model.utils.MyConnector
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsMembershipViewModel

internal class GetMembershipListDataSource :
    PositionalDataSource<MembershipListItem?>() {
    override fun loadInitial(
        params: LoadInitialParams,
        callback: LoadInitialCallback<MembershipListItem?>
    ) {
        GlobalScope.launch{
            // request
            val connector = MyConnector()
            val response =
                connector.requestMembershipList(params.requestedStartPosition, params.requestedLoadSize)
            Log.d("surprise", "GetMembershipListDataSource.kt 25: $response")
            if (response.isNotEmpty()) {
                val builder = GsonBuilder()
                val gson: Gson = builder.create()
                val result: GetMembershipDataResponse = gson.fromJson(
                    response,
                    GetMembershipDataResponse::class.java
                )
                result.modify()
                AccrualsMembershipViewModel.isLoaded.postValue(true)
                callback.onResult(result.list, 0, result.count)
            } else {
                callback.onResult(listOf(), 0)
            }
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<MembershipListItem?>) {
        // request
        val connector = MyConnector()
        val response = connector.requestMembershipList(params.startPosition, params.loadSize)
        if (response.isNotEmpty()) {
            val builder = GsonBuilder()
            val gson: Gson = builder.create()
            val result: GetMembershipDataResponse = gson.fromJson(
                response,
                GetMembershipDataResponse::class.java
            )
            result.modify()
            callback.onResult(result.list)
        } else {
            callback.onResult(listOf())
        }
    }
}