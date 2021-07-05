package net.veldor.oblepiha_kotlin.model.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.BR
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.PowerItemViewBinding
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler

class PowerListAdapter constructor(diffUtilCallback: DiffUtil.ItemCallback<PowerData>) :
    PagedListAdapter<PowerData, PowerListAdapter.PowerListViewHolder>(diffUtilCallback) {
    private var mLayoutInflater: LayoutInflater? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PowerListViewHolder {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: PowerItemViewBinding =
            DataBindingUtil.inflate(mLayoutInflater!!, R.layout.power_item_view, parent, false)
        return PowerListViewHolder(binding)
    }

    class PowerListViewHolder (binding: PowerItemViewBinding) : RecyclerView.ViewHolder(binding.root){

        private var mBinding: PowerItemViewBinding = binding
        private var root: View = binding.root

        fun bind(item: PowerData?) {
            mBinding.setVariable(BR.item, item!!)
            mBinding.executePendingBindings()
            mBinding.powerSpend.text = GrammarHandler.convertWatt(((item.data.toDouble() - item.previousData.toDouble()).toString()))
            mBinding.getDataTime.text = GrammarHandler.timeToString(item.timestamp)
            root.setOnClickListener{
                Toast.makeText(App.instance, item.data, Toast.LENGTH_SHORT).show()
            }
            root.setOnLongClickListener {
                val dao = App.instance.database.powerDao()
                dao.delete(item)
                Toast.makeText(App.instance, "Deleted", Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    override fun onBindViewHolder(holder: PowerListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}