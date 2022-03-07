package net.veldor.oblepiha_kotlin.view.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
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
import net.veldor.oblepiha_kotlin.databinding.FragmentPowerListBinding
import net.veldor.oblepiha_kotlin.databinding.PowerListItemViewBinding
import net.veldor.oblepiha_kotlin.model.adapters.PowerListAdapter
import net.veldor.oblepiha_kotlin.model.adapters.PowerListItemsAdapter
import net.veldor.oblepiha_kotlin.model.data_source.GetPowerListDataSource
import net.veldor.oblepiha_kotlin.model.data_source.MyPositionalDataSource
import net.veldor.oblepiha_kotlin.model.data_source.PowerDataUtilCallback
import net.veldor.oblepiha_kotlin.model.data_source.PowerListItemDiffCallback
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData
import net.veldor.oblepiha_kotlin.model.selections.PowerListItem
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler
import net.veldor.oblepiha_kotlin.model.view_models.*
import net.veldor.oblepiha_kotlin.view.ContentActivity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AccrualsPowerFragment : Fragment() {

    private var _binding: FragmentPowerListBinding? = null
    private lateinit var root: View
    private lateinit var recyclerView: RecyclerView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentPowerListBinding.inflate(inflater, container, false)
        root = binding.root

        handleRecycler()
        return root
    }

    override fun onPause() {
        super.onPause()
        removeObservers()
    }

    private fun removeObservers() {
        AccrualsPowerViewModel.selectedForDetails.removeObservers(viewLifecycleOwner)
    }

    override fun onResume() {
        super.onResume()
        setupObservers()
    }

    private fun setupObservers() {
        AccrualsPowerViewModel.selectedForDetails.observe(viewLifecycleOwner) {
            if (it != null) {
                // show power accrual list
                val navController = (requireActivity() as ContentActivity).navController
                if (navController.currentDestination?.id == R.id.navigation_accruals_power) {
                    AccrualsPowerDetailsViewModel.selectedForDetails = it
                    navController.navigate(R.id.action_show_power_details)
                }
                AccrualsPowerViewModel.selectedForDetails.removeObservers(viewLifecycleOwner)
                AccrualsPowerViewModel.selectedForDetails.value = null
            }
        }

        AccrualsPowerViewModel.isLoaded.observe(viewLifecycleOwner) {
            if (it > 0) {
                AccrualsPowerViewModel.isLoaded.removeObservers(viewLifecycleOwner)
                binding.contentLoadingProgressView.visibility = View.GONE
                binding.noMessagesText.visibility = View.GONE
            }
            else{
                binding.noMessagesText.visibility = View.VISIBLE
            }

        }
    }

    private fun handleRecycler() {
        // DataSource
        val dataSource = GetPowerListDataSource()
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()
        val pageList: PagedList<PowerListItem?> = PagedList.Builder(dataSource, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setNotifyExecutor(MainThreadExecutor())
            .build()
        recyclerView = root.findViewById(R.id.dataList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PowerListItemsAdapter(PowerListItemDiffCallback())
        adapter.submitList(pageList)
        recyclerView.adapter = adapter
    }

    internal class MainThreadExecutor : Executor {
        private val handler: Handler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            handler.post(command)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (requireActivity() as ContentActivity).navController.navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }
}