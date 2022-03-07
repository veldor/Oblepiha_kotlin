package net.veldor.oblepiha_kotlin.model.data_source

import androidx.recyclerview.widget.DiffUtil
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData
import net.veldor.oblepiha_kotlin.model.selections.MessageItem
import net.veldor.oblepiha_kotlin.model.selections.PowerListItem

class MessagesListItemDiffCallback : DiffUtil.ItemCallback<MessageItem>() {

    override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem.id == newItem.id
    }

}