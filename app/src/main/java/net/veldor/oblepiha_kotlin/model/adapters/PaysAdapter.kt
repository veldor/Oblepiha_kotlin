package net.veldor.oblepiha_kotlin.model.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.BR
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.BillEntityItemViewBinding
import net.veldor.oblepiha_kotlin.databinding.PaysListItemViewBinding
import net.veldor.oblepiha_kotlin.model.selections.BillEntity
import net.veldor.oblepiha_kotlin.model.selections.PaymentItem

class PaysAdapter(private var list: List<PaymentItem?>) :
    RecyclerView.Adapter<PaysAdapter.ViewHolder>() {
    private var mLayoutInflater: LayoutInflater? = null

    class ViewHolder(binding: PaysListItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        private var binded: PaymentItem? = null
        private var mBinding: PaysListItemViewBinding = binding

        fun bind(get: PaymentItem?) {
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
        val binding: PaysListItemViewBinding =
            DataBindingUtil.inflate(
                mLayoutInflater!!,
                R.layout.pays_list_item_view,
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