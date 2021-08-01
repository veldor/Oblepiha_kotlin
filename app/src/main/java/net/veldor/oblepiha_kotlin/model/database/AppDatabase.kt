package net.veldor.oblepiha_kotlin.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import net.veldor.oblepiha_kotlin.model.database.dao.PowerDao
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData

@Database(entities = [PowerData::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun powerDao(): PowerDao
}