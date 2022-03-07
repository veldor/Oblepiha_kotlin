package net.veldor.oblepiha_kotlin.model.delegates

import net.veldor.oblepiha_kotlin.model.selections.MessageItem

interface MessageDelegate {
    fun messageClicked(item: MessageItem?)
    fun listIsEmpty()
    fun listIsNotEmpty()
}