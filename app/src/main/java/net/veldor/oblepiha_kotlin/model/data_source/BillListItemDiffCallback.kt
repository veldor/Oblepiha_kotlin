package net.veldor.oblepiha_kotlin.model.data_source

import androidx.recyclerview.widget.DiffUtil
import net.veldor.oblepiha_kotlin.model.selections.BillListItem

class BillListItemDiffCallback : DiffUtil.ItemCallback<BillListItem>() {

    override fun areItemsTheSame(oldItem: BillListItem, newItem: BillListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BillListItem, newItem: BillListItem): Boolean {
        return oldItem.id == newItem.id
    }

}