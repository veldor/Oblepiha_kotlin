package net.veldor.oblepiha_kotlin.model.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.BR
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.BillEntityItemViewBinding
import net.veldor.oblepiha_kotlin.databinding.BillTransactionsItemViewBinding
import net.veldor.oblepiha_kotlin.model.selections.BillEntity
import net.veldor.oblepiha_kotlin.model.selections.BillTransaction

class BillTransactionsAdapter(private var list: List<BillTransaction?>) :
    RecyclerView.Adapter<BillTransactionsAdapter.ViewHolder>() {
    private var mLayoutInflater: LayoutInflater? = null

    class ViewHolder(binding: BillTransactionsItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        private var binded: BillTransaction? = null
        private var mBinding: BillTransactionsItemViewBinding = binding

        fun bind(get: BillTransaction?) {
            binded = get
            mBinding.setVariable(BR.item, get)
            mBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: BillTransactionsItemViewBinding =
            DataBindingUtil.inflate(
                mLayoutInflater!!,
                R.layout.bill_transactions_item_view,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }
}