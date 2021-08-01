package net.veldor.oblepiha_kotlin.model.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = arrayOf("timestamp"), unique = true)])
class PowerData {
    @PrimaryKey(autoGenerate = true)
    var id = 0

    var data = ""
    var previousData = ""
    var timestamp: Long = -1
}