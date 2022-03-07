package net.veldor.oblepiha_kotlin.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.BR
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.MessageItemViewBinding
import net.veldor.oblepiha_kotlin.model.delegates.MessageDelegate
import net.veldor.oblepiha_kotlin.model.selections.MessageItem

class MessagesAdapter constructor(
    diffUtilCallback: DiffUtil.ItemCallback<MessageItem>,
    val delegate: MessageDelegate
) :
    PagedListAdapter<MessageItem, MessagesAdapter.MessagesViewHolder>(diffUtilCallback) {
    private var mLayoutInflater: LayoutInflater =
        LayoutInflater.from(App.instance.applicationContext)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagesViewHolder {
        val binding: MessageItemViewBinding =
            MessageItemViewBinding.inflate(mLayoutInflater, parent, false)
        return MessagesViewHolder(binding)
    }


    inner class MessagesViewHolder(binding: MessageItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var mItem: MessageItem? = null
        private var mBinding: MessageItemViewBinding = binding

        init {
            mBinding.rootView.setOnClickListener {
                delegate.messageClicked(mItem)
            }
        }

        fun bind(item: MessageItem?) {
            mItem = item
            if (item != null) {
                if (item.is_read == "1") {
                    mBinding.notificationUnreadBadge.visibility = View.INVISIBLE
                } else {
                    mBinding.notificationUnreadBadge.visibility = View.VISIBLE
                }
                if (item.type == "broadcast") {
                    mBinding.notificationTypeBadge.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            App.instance.resources,
                            R.drawable.ic_baseline_campaign_24,
                            null
                        )
                    )
                    mBinding.notificationTypeBadge.setBackgroundResource(R.drawable.broadcast_badge)
                } else if (item.type == "pay_confirm") {
                    mBinding.notificationTypeBadge.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            App.instance.resources,
                            R.drawable.ic_baseline_check_circle_24,
                            null
                        )
                    )
                    mBinding.notificationTypeBadge.setBackgroundResource(R.drawable.pay_confirm_badge)
                } else if (item.type == "new_bill") {
                    mBinding.notificationTypeBadge.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            App.instance.resources,
                            R.drawable.ic_baseline_add_card_24,
                            null
                        )
                    )
                    mBinding.notificationTypeBadge.setBackgroundResource(R.drawable.new_bill_badge)
                } else if (item.type == "power_use") {
                    mBinding.notificationTypeBadge.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            App.instance.resources,
                            R.drawable.ic_baseline_electrical_services_24,
                            null
                        )
                    )
                    mBinding.notificationTypeBadge.setBackgroundResource(R.drawable.round_image)
                } else {
                    mBinding.notificationTypeBadge.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            App.instance.resources,
                            R.drawable.ic_baseline_message_24,
                            null
                        )
                    )
                    mBinding.notificationTypeBadge.setBackgroundResource(R.drawable.misc_message_badge)
                }

                mBinding.loader.stopShimmer()
                mBinding.loader.hideShimmer()
                mBinding.setVariable(BR.item, item)
                mBinding.executePendingBindings()
            } else {
                mBinding.notificationTime.text = ""
                mBinding.messageTitle.text = ""
                mBinding.messageBody.text = ""
                mBinding.loader.startShimmer()
            }
        }
    }

    override fun onBindViewHolder(holder: MessagesViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun markAsRead(item: MessageItem) {
        val items = currentList
        var position = 0
        items?.forEach {
            if (it.equals(item)) {
                it.is_read = "1"
                notifyItemChanged(position)
                return
            }
            position++
        }
    }

    fun delete(item: MessageItem) {
    }

    fun markBroadcastAsRead(broadcastId: String) {
        val items = currentList
        var position = 0
        items?.forEach {
            if (it.broadcast_id == broadcastId) {
                it.is_read = "1"
                notifyItemChanged(position)
                return
            }
            position++
        }
    }

    fun markAsRead(id: Int) {
        val items = currentList
        var position = 0
        items?.forEach {
            if (it.id == id) {
                it.is_read = "1"
                notifyItemChanged(position)
                return
            }
            position++
        }
    }
}