package net.veldor.oblepiha_kotlin.model.data_source

import androidx.recyclerview.widget.DiffUtil
import net.veldor.oblepiha_kotlin.model.selections.MembershipListItem

class MembershipListItemDiffCallback : DiffUtil.ItemCallback<MembershipListItem>() {

    override fun areItemsTheSame(oldItem: MembershipListItem, newItem: MembershipListItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MembershipListItem, newItem: MembershipListItem): Boolean {
        return oldItem.quarter == newItem.quarter
    }

}