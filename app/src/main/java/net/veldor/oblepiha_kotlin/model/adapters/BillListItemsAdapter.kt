package net.veldor.oblepiha_kotlin.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.BR
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.BillListItemViewBinding
import net.veldor.oblepiha_kotlin.model.selections.BillListItem
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler
import net.veldor.oblepiha_kotlin.model.view_models.BillsListViewModel
import java.util.*

class BillListItemsAdapter constructor(diffUtilCallback: DiffUtil.ItemCallback<BillListItem>) :
    PagedListAdapter<BillListItem, BillListItemsAdapter.BillListViewHolder>(diffUtilCallback) {
    private var mLayoutInflater: LayoutInflater? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillListViewHolder {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: BillListItemViewBinding =
            DataBindingUtil.inflate(mLayoutInflater!!, R.layout.bill_list_item_view, parent, false)
        return BillListViewHolder(binding)
    }

    class BillListViewHolder(binding: BillListItemViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var mBinding: BillListItemViewBinding = binding

        fun bind(item: BillListItem?) {
            if (item != null) {
                mBinding.loader.stopShimmer()
                mBinding.loader.hideShimmer()
                mBinding.setVariable(BR.item, item)
                mBinding.executePendingBindings()
                mBinding.cost.text = GrammarHandler.showPrice(item.totalSumm.toInt())
                when {
                    item.toDeposit.toInt() > 0 -> {
                        mBinding.depositState.visibility = View.VISIBLE
                        mBinding.depositState.text = String.format(
                            App.instance.getString(R.string.depisit_incerase_title),
                            GrammarHandler.showPrice(item.toDeposit.toInt())
                        )
                        mBinding.depositState.setTextColor(
                            ResourcesCompat.getColor(
                                App.instance.resources,
                                R.color.text_success,
                                null
                            )
                        )
                    }
                    item.depositUsed.toInt() > 0 -> {
                        mBinding.depositState.visibility = View.VISIBLE
                        mBinding.depositState.text = String.format(
                            App.instance.getString(R.string.deposit_decreese_titile),
                            GrammarHandler.showPrice(item.depositUsed.toInt())
                        )
                        mBinding.depositState.setTextColor(
                            ResourcesCompat.getColor(
                                App.instance.resources,
                                R.color.text_warning,
                                null
                            )
                        )

                    }
                    else -> {
                        mBinding.depositState.visibility = View.GONE
                    }
                }
                if (item.payedSumm.toInt() + item.depositUsed.toInt() >= item.totalSumm.toInt()) {
                    mBinding.payState.text = "Оплачено"
                    mBinding.payState.setTextColor(
                        ResourcesCompat.getColor(
                            App.instance.resources,
                            R.color.text_success,
                            null
                        )
                    )

                }
                else if(item.payedSumm.toInt() == 0){
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
                        String.format(
                            Locale.ENGLISH,
                            "Оплачено:\n %s",
                            GrammarHandler.showPrice(item.payedSumm.toInt() + item.depositUsed.toInt())
                        )
                    mBinding.payState.setTextColor(
                        ResourcesCompat.getColor(
                            App.instance.resources,
                            R.color.teal_200,
                            null
                        )
                    )
                }
                mBinding.rootView.setOnClickListener {
                    // open details
                    BillsListViewModel.selectedForDetails.postValue(item)
                }
            } else {
                mBinding.cost.text = ""
                mBinding.payState.text = ""
                mBinding.loader.startShimmer()
            }
        }
    }

    override fun onBindViewHolder(holder: BillListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}