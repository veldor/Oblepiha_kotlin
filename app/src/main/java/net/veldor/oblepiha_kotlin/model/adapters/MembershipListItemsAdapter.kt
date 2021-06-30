package net.veldor.oblepiha_kotlin.model.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.BR
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.MembershipListItemViewBinding
import net.veldor.oblepiha_kotlin.model.selections.MembershipListItem
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsMembershipViewModel
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsPowerViewModel
import java.util.*

class MembershipListItemsAdapter constructor(diffUtilCallback: DiffUtil.ItemCallback<MembershipListItem>) :
    PagedListAdapter<MembershipListItem, MembershipListItemsAdapter.MembershipListViewHolder>(
        diffUtilCallback
    ) {
    private var mLayoutInflater: LayoutInflater? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembershipListViewHolder {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: MembershipListItemViewBinding =
            DataBindingUtil.inflate(
                mLayoutInflater!!,
                R.layout.membership_list_item_view,
                parent,
                false
            )
        return MembershipListViewHolder(binding)
    }

    class MembershipListViewHolder(binding: MembershipListItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var mBinding: MembershipListItemViewBinding = binding

        fun bind(item: MembershipListItem?) {
            if (item != null) {
                mBinding.loader.stopShimmer()
                mBinding.loader.hideShimmer()
                mBinding.setVariable(BR.item, item)
                mBinding.executePendingBindings()
                if (item.payed == item.accrued) {
                    mBinding.payState.text = "Оплачено"
                    mBinding.payState.setTextColor(
                        ResourcesCompat.getColor(
                            App.instance.resources,
                            R.color.text_success,
                            null
                        )
                    )

                } else {
                    mBinding.payState.text =
                        String.format(Locale.ENGLISH, "Оплачено %s", item.payed)
                    mBinding.payState.setTextColor(
                        ResourcesCompat.getColor(
                            App.instance.resources,
                            R.color.text_warning,
                            null
                        )
                    )
                }
                mBinding.rootView.setOnClickListener {
                    Log.d("surprise", "PowerListItemsAdapter.kt 45: click")
                    // open details
                    AccrualsMembershipViewModel.selectedForDetails.postValue(item)
                }
            } else {
                mBinding.monthView.text = ""
                mBinding.cost.text = ""
                mBinding.payState.text = ""
                mBinding.loader.startShimmer()
            }
        }
    }

    override fun onBindViewHolder(holder: MembershipListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}