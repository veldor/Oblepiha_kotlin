package net.veldor.oblepiha_kotlin.model.delegates

import net.veldor.oblepiha_kotlin.model.selections.BillListItem
import net.veldor.oblepiha_kotlin.model.selections.MessageItem

interface BillInfoDelegate {
    fun billInfoReceived(item: BillListItem?)
}