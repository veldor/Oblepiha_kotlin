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
import net.veldor.oblepiha_kotlin.databinding.TargetListItemViewBinding
import net.veldor.oblepiha_kotlin.model.selections.TargetListItem
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsTargetViewModel
import java.util.*

class TargetListItemsAdapter constructor(diffUtilCallback: DiffUtil.ItemCallback<TargetListItem>) :
    PagedListAdapter<TargetListItem, TargetListItemsAdapter.TargetListViewHolder>(
        diffUtilCallback
    ) {
    private var mLayoutInflater: LayoutInflater? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TargetListViewHolder {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: TargetListItemViewBinding =
            DataBindingUtil.inflate(
                mLayoutInflater!!,
                R.layout.target_list_item_view,
                parent,
                false
            )
        return TargetListViewHolder(binding)
    }

    class TargetListViewHolder(binding: TargetListItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var mBinding: TargetListItemViewBinding = binding

        fun bind(item: TargetListItem?) {
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

                }
                else if(item.payed.startsWith("0")){
                    mBinding.payState.text = "Не оплачено"
                    mBinding.payState.setTextColor(
                        ResourcesCompat.getColor(
                            App.instance.resources,
                            R.color.text_warning,
                            null
                        )
                    )
                }
                else {
                    mBinding.payState.text =
                        String.format(Locale.ENGLISH, "Оплачено:\n %s", item.payed)
                    mBinding.payState.setTextColor(
                        ResourcesCompat.getColor(
                            App.instance.resources,
                            R.color.text_warning,
                            null
                        )
                    )
                }
                mBinding.rootView.setOnClickListener {
                    // open details
                    AccrualsTargetViewModel.selectedForDetails.postValue(item)
                }
            } else {
                mBinding.yearView.text = ""
                mBinding.cost.text = ""
                mBinding.payState.text = ""
                mBinding.loader.startShimmer()
            }
        }
    }

    override fun onBindViewHolder(holder: TargetListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}