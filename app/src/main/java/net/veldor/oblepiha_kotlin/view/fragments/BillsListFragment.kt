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
import net.veldor.oblepiha_kotlin.databinding.FragmentBillsListBinding
import net.veldor.oblepiha_kotlin.model.adapters.BillListItemsAdapter
import net.veldor.oblepiha_kotlin.model.data_source.BillListItemDiffCallback
import net.veldor.oblepiha_kotlin.model.data_source.BillsListDataSource
import net.veldor.oblepiha_kotlin.model.selections.BillListItem
import net.veldor.oblepiha_kotlin.model.view_models.BillDetailsViewModel
import net.veldor.oblepiha_kotlin.model.view_models.BillsListViewModel
import net.veldor.oblepiha_kotlin.view.ContentActivity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class BillsListFragment : Fragment() {

    private var _binding: FragmentBillsListBinding? = null
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
        _binding = FragmentBillsListBinding.inflate(inflater, container, false)
        root = binding.root

        handleRecycler()
        return root
    }

    override fun onPause() {
        super.onPause()
        removeObservers()
    }

    private fun removeObservers() {
        BillsListViewModel.selectedForDetails.removeObservers(viewLifecycleOwner)
    }

    override fun onResume() {
        super.onResume()
        setupObservers()
    }

    private fun setupObservers() {
        BillsListViewModel.selectedForDetails.observe(viewLifecycleOwner) {
            if (it != null) {
                val navController = (requireActivity() as ContentActivity).navController
                if (navController.currentDestination?.id == R.id.navigation_bills_list) {
                    BillDetailsViewModel.selectedForDetails = it
                    navController.navigate(R.id.action_open_bill_details)
                }
                BillsListViewModel.selectedForDetails.removeObservers(viewLifecycleOwner)
                BillsListViewModel.selectedForDetails.value = null
            }
        }
        BillsListViewModel.isLoaded.observe(viewLifecycleOwner) {
            BillsListViewModel.isLoaded.removeObservers(viewLifecycleOwner)
            BillsListViewModel.isLoaded.value = false
            binding.contentLoadingProgressView.visibility = View.GONE
            if (it) {
                binding.noMessagesText.visibility = View.VISIBLE
            } else {
                binding.noMessagesText.visibility = View.GONE
            }
        }
    }

    private fun handleRecycler() {
        // DataSource
        val dataSource = BillsListDataSource()
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()
        val pageList: PagedList<BillListItem?> = PagedList.Builder(dataSource, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setNotifyExecutor(MainThreadExecutor())
            .build()
        recyclerView = root.findViewById(R.id.dataList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = BillListItemsAdapter(BillListItemDiffCallback())
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