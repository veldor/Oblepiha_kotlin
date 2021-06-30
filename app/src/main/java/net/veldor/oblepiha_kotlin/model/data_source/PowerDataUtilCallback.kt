package net.veldor.oblepiha_kotlin.model.data_source

import androidx.recyclerview.widget.DiffUtil
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData

class PowerDataUtilCallback : DiffUtil.ItemCallback<PowerData>() {

    override fun areItemsTheSame(oldItem: PowerData, newItem: PowerData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PowerData, newItem: PowerData): Boolean {
        return oldItem.timestamp == newItem.timestamp
    }

}