package net.veldor.oblepiha_kotlin.model.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.BR
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.PowerItemViewBinding
import net.veldor.oblepiha_kotlin.model.selections.PowerListItem
import net.veldor.oblepiha_kotlin.model.selections.UsedPowerItem
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler
import java.util.*

class PowerListAdapter(private var list: List<UsedPowerItem>) :
    RecyclerView.Adapter<PowerListAdapter.ViewHolder>() {
    private var mLayoutInflater: LayoutInflater? = null

    class ViewHolder(binding: PowerItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        private var binded: UsedPowerItem? = null
        private var mBinding: PowerItemViewBinding = binding

        fun bind(get: UsedPowerItem) {
            binded = get
            mBinding.setVariable(BR.item, get)
            mBinding.executePendingBindings()
            mBinding.powerSpend.text = String.format(Locale.ENGLISH, "%.3f кВт(%s р.)", get.used, get.cost)

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: PowerItemViewBinding =
            DataBindingUtil.inflate(
                mLayoutInflater!!,
                R.layout.power_item_view,
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

    fun setItems(list: List<UsedPowerItem>?) {
        if(list != null){
            this.list = list
            notifyDataSetChanged()
        }
    }
}