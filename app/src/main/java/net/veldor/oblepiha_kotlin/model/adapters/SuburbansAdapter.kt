package net.veldor.oblepiha_kotlin.model.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.BR
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.SuburbanItemViewBinding
import net.veldor.oblepiha_kotlin.model.selections.Segment
import net.veldor.oblepiha_kotlin.model.selections.SuburbanSchedule
import net.veldor.oblepiha_kotlin.model.utils.SuburbanHandler

class SuburbansAdapter(private var list: SuburbanSchedule) :
    RecyclerView.Adapter<SuburbansAdapter.ViewHolder>() {
    private var mLayoutInflater: LayoutInflater? = null

    class ViewHolder(binding: SuburbanItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        private var binded: Segment? = null
        private var mBinding: SuburbanItemViewBinding = binding

        fun bind(get: Segment?) {
            binded = get
            mBinding.setVariable(BR.item, get)
            mBinding.executePendingBindings()
            val text = SuburbanHandler(null).getInfo(get)
            mBinding.fromCityTrainTime.text = text
            if(text.endsWith("ушла")){
                mBinding.trainFromCityContainer.setBackgroundColor(ResourcesCompat.getColor(App.instance.resources, R.color.warning_background, null))
            }
            else{
                mBinding.trainFromCityContainer.background = ResourcesCompat.getDrawable(App.instance.resources, R.color.card_background, null)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.context)
        }
        val binding: SuburbanItemViewBinding =
            DataBindingUtil.inflate(
                mLayoutInflater!!,
                R.layout.suburban_item_view,
                parent,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.segments!![position])
    }

    override fun getItemCount(): Int {
        return list.segments!!.size
    }
}