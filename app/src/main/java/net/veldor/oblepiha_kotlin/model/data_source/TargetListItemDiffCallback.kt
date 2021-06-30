package net.veldor.oblepiha_kotlin.model.data_source

import androidx.recyclerview.widget.DiffUtil
import net.veldor.oblepiha_kotlin.model.selections.MembershipListItem
import net.veldor.oblepiha_kotlin.model.selections.TargetListItem

class TargetListItemDiffCallback : DiffUtil.ItemCallback<TargetListItem>() {

    override fun areItemsTheSame(oldItem: TargetListItem, newItem: TargetListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TargetListItem, newItem: TargetListItem): Boolean {
        return oldItem.year == newItem.year
    }

}