package net.veldor.oblepiha_kotlin.view.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.FragmentTargetListBinding
import net.veldor.oblepiha_kotlin.model.adapters.TargetListItemsAdapter
import net.veldor.oblepiha_kotlin.model.data_source.GetTargetListDataSource
import net.veldor.oblepiha_kotlin.model.data_source.TargetListItemDiffCallback
import net.veldor.oblepiha_kotlin.model.selections.TargetListItem
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsPowerViewModel
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsTargetDetailsViewModel
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsTargetViewModel
import net.veldor.oblepiha_kotlin.view.ContentActivity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AccrualsTargetFragment : Fragment() {

    private var _binding: FragmentTargetListBinding? = null
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
        _binding = FragmentTargetListBinding.inflate(inflater, container, false)
        root = binding.root

        handleRecycler()
        return root
    }

    override fun onPause() {
        super.onPause()
        removeObservers()
    }

    private fun removeObservers() {
        AccrualsTargetViewModel.selectedForDetails.removeObservers(viewLifecycleOwner)
    }

    override fun onResume() {
        super.onResume()
        setupObservers()
    }

    private fun setupObservers() {
        AccrualsTargetViewModel.selectedForDetails.observe(viewLifecycleOwner, {
            if (it != null) {
                // show power accrual list
                val navController = (requireActivity() as ContentActivity).navController
                if (navController.currentDestination?.id == R.id.navigation_accruals_target) {
                    AccrualsTargetDetailsViewModel.selectedForDetails = it
                    navController.navigate(R.id.action_show_target_details)
                }
                AccrualsTargetViewModel.selectedForDetails.removeObservers(viewLifecycleOwner)
                AccrualsTargetViewModel.selectedForDetails.value = null
            }
        })
        AccrualsTargetViewModel.isLoaded.observe(viewLifecycleOwner, {
            if(it){
                AccrualsTargetViewModel.isLoaded.removeObservers(viewLifecycleOwner)
                AccrualsTargetViewModel.isLoaded.value = false
                binding.contentLoadingProgressView.visibility = View.GONE
            }
        })
    }

    private fun handleRecycler() {
        // DataSource
        val dataSource = GetTargetListDataSource()
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()
        val pageList: PagedList<TargetListItem?> = PagedList.Builder(dataSource, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setNotifyExecutor(MainThreadExecutor())
            .build()
        recyclerView = root.findViewById(R.id.dataList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = TargetListItemsAdapter(TargetListItemDiffCallback())
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