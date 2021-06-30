package net.veldor.oblepiha_kotlin.view.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.FragmentAccrualsBinding
import net.veldor.oblepiha_kotlin.databinding.FragmentBillsBinding
import net.veldor.oblepiha_kotlin.databinding.FragmentPowerBinding
import net.veldor.oblepiha_kotlin.model.adapters.PowerListAdapter
import net.veldor.oblepiha_kotlin.model.data_source.MyPositionalDataSource
import net.veldor.oblepiha_kotlin.model.data_source.PowerDataUtilCallback
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData
import net.veldor.oblepiha_kotlin.model.view_models.BillsViewModel
import net.veldor.oblepiha_kotlin.model.view_models.PowerViewModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class BillsFragment : Fragment() {

    private lateinit var viewModel: BillsViewModel
    private var _binding: FragmentBillsBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this).get(BillsViewModel::class.java)
        _binding = FragmentBillsBinding.inflate(inflater, container, false)
        binding.handler = this
        val root: View = binding.root
        viewModel.state.observe(viewLifecycleOwner, {
            if(it != null){
                binding.payedBillsText.text = it.payed.toString()
                binding.unpayedBillsText.text = it.upnayed.toString()
            }
        })
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateInfo()
    }

    fun showPayedBills(){

    }
    fun showUnpayedBills(){

    }
}