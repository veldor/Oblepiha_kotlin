package net.veldor.oblepiha_kotlin.model.data_source

import androidx.paging.PositionalDataSource
import net.veldor.oblepiha_kotlin.model.database.dao.PowerDao
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData

internal class MyPositionalDataSource(private val dao: PowerDao) :
    PositionalDataSource<PowerData?>() {
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<PowerData?>) {
        val result: List<PowerData?> =
            dao.getPositionalData(params.requestedStartPosition, params.requestedLoadSize)
        callback.onResult(result, 0)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<PowerData?>) {
        val result: List<PowerData?> = dao.getPositionalData(params.startPosition, params.loadSize)
        callback.onResult(result)
    }
}