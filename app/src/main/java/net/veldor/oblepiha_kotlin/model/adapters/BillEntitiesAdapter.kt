package net.veldor.oblepiha_kotlin.model.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.BR
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.BillEntityItemViewBinding
import net.veldor.oblepiha_kotlin.model.selections.BillEntity

class BillEntitiesAdapter(private var list: List<BillEntity?>) :
    RecyclerView.Adapter<BillEntitiesAdapter.ViewHolder>() {
    private var mLayoutInflater: LayoutInflater? = null

    class ViewHolder(binding: BillEntityItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        private var binded: BillEntity? = null
        private var mBinding: BillEntityItemViewBinding = binding

        fun bind(get: BillEntity?) {
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
        val binding: BillEntityItemViewBinding =
            DataBindingUtil.inflate(
                mLayoutInflater!!,
                R.layout.bill_entity_item_view,
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