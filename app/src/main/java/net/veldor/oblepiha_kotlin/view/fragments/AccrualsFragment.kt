package net.veldor.oblepiha_kotlin.view.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.FragmentAccrualsBinding
import net.veldor.oblepiha_kotlin.databinding.FragmentPowerBinding
import net.veldor.oblepiha_kotlin.model.adapters.PowerListAdapter
import net.veldor.oblepiha_kotlin.model.data_source.MyPositionalDataSource
import net.veldor.oblepiha_kotlin.model.data_source.PowerDataUtilCallback
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsViewModel
import net.veldor.oblepiha_kotlin.model.view_models.PowerViewModel
import net.veldor.oblepiha_kotlin.view.ContentActivity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AccrualsFragment : Fragment() {

    private lateinit var viewModel: AccrualsViewModel
    private var _binding: FragmentAccrualsBinding? = null
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccrualsBinding.inflate(inflater, container, false)
        binding.handler = this
        val root: View = binding.root
        viewModel =
            ViewModelProvider(this).get(AccrualsViewModel::class.java)

        swipeRefreshLayout = root.findViewById(R.id.swipeLayout)
        // handle refresh layout
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.checkStatus()
        }

        viewModel.status.observe(viewLifecycleOwner, {
            swipeRefreshLayout.isRefreshing = false
            binding.totalDuty.text = GrammarHandler.showPrice(it.totalDuty)
            binding.powerDuty.text = GrammarHandler.showPrice(it.powerDuty)
            binding.membershipDuty.text = GrammarHandler.showPrice(it.membershipDuty)
            binding.targetDuty.text = GrammarHandler.showPrice(it.targetDuty)
            binding.totalDutyLoader.hideShimmer()
            binding.membershipLoader.hideShimmer()
            binding.powerLoader.hideShimmer()
            binding.targetLoader.hideShimmer()
        })
        return root
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showDetails(view: View){
        if(view.id == R.id.powerContainer){
            // show power accrual list
            val navController = (requireActivity() as ContentActivity).navController
            if (navController.currentDestination?.id == R.id.navigation_accruals) {
                navController.navigate(R.id.action_show_power_accruals)
            }
        }
        else if(view.id == R.id.membershipContainer){
            // show power accrual list
            val navController = (requireActivity() as ContentActivity).navController
            if (navController.currentDestination?.id == R.id.navigation_accruals) {
                navController.navigate(R.id.action_show_membership_accruals)
            }
        }
        else if(view.id == R.id.targetContainer){
            // show power accrual list
            val navController = (requireActivity() as ContentActivity).navController
            if (navController.currentDestination?.id == R.id.navigation_accruals) {
                navController.navigate(R.id.action_show_target_accruals)
            }
        }
    }
}