package net.veldor.oblepiha_kotlin.model.data_source

import androidx.recyclerview.widget.DiffUtil
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData
import net.veldor.oblepiha_kotlin.model.selections.PowerListItem

class PowerListItemDiffCallback : DiffUtil.ItemCallback<PowerListItem>() {

    override fun areItemsTheSame(oldItem: PowerListItem, newItem: PowerListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PowerListItem, newItem: PowerListItem): Boolean {
        return oldItem.month == newItem.month
    }

}