package net.veldor.oblepiha_kotlin.model.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class PowerData {
    @PrimaryKey(autoGenerate = true)
    var id = 0
    var data = ""
    var previousData = ""
    var timestamp: Long = -1
}