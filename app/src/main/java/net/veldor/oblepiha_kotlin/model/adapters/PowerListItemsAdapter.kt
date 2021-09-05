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
import net.veldor.oblepiha_kotlin.databinding.PowerListItemViewBinding
import net.veldor.oblepiha_kotlin.model.selections.PowerListItem
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsPowerViewModel
import java.util.*

class PowerListItemsAdapter constructor(diffUtilCallback: DiffUtil.ItemCallback<PowerListItem>) :
    PagedListAdapter<PowerListItem, PowerListItemsAdapter.PowerListViewHolder>(diffUtilCallback) {
    private var mLayoutInflater: LayoutInflater? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PowerListViewHolder {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: PowerListItemViewBinding =
            DataBindingUtil.inflate(mLayoutInflater!!, R.layout.power_list_item_view, parent, false)
        return PowerListViewHolder(binding)
    }

    class PowerListViewHolder(binding: PowerListItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var mBinding: PowerListItemViewBinding = binding

        fun bind(item: PowerListItem?) {
            if (item != null) {
                mBinding.loader.stopShimmer()
                mBinding.loader.hideShimmer()
                mBinding.setVariable(BR.item, item)
                mBinding.executePendingBindings()
                if (item.payed == item.totalPay) {
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
                    Log.d("surprise", "PowerListItemsAdapter.kt 45: click")
                    // open details
                    AccrualsPowerViewModel.selectedForDetails.postValue(item)
                }
            } else {
                mBinding.monthView.text = ""
                mBinding.powerSpend.text = ""
                mBinding.cost.text = ""
                mBinding.payState.text = ""
                mBinding.loader.startShimmer()
            }
        }
    }

    override fun onBindViewHolder(holder: PowerListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}