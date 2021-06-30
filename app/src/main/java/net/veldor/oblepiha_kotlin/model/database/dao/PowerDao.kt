package net.veldor.oblepiha_kotlin.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData

@Dao
interface PowerDao {
    @Query("SELECT * FROM powerData WHERE id = :id")
    fun getBookById(id: String?): PowerData?

    @get:Query("SELECT * FROM powerData ORDER BY timestamp DESC")
    val allData: List<PowerData?>?

    @Query("SELECT * FROM powerData WHERE timestamp = :timestamp")
    fun getByTimestamp(timestamp: Long): PowerData?

    @get:Query("SELECT * FROM powerData ORDER BY timestamp LIMIT 1")
    val lastRegistered: PowerData?

    @Insert
    fun insert(data: PowerData)

    @Query("SELECT * FROM powerData ORDER BY timestamp DESC LIMIT :requestedLoadSize OFFSET :requestedStartPosition")
    fun getPositionalData(requestedStartPosition: Int, requestedLoadSize: Int): List<PowerData?>

    @Query("SELECT * FROM powerData WHERE timestamp > :startTime")
    fun getFromTimestamp(startTime: Long): List<PowerData?>
}