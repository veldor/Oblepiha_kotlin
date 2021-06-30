package net.veldor.oblepiha_kotlin.view.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.FragmentMembershipListBinding
import net.veldor.oblepiha_kotlin.model.adapters.MembershipListItemsAdapter
import net.veldor.oblepiha_kotlin.model.data_source.GetMembershipListDataSource
import net.veldor.oblepiha_kotlin.model.data_source.MembershipListItemDiffCallback
import net.veldor.oblepiha_kotlin.model.selections.MembershipListItem
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsMembershipDetailsViewModel
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsMembershipViewModel
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsPowerDetailsViewModel
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsPowerViewModel
import net.veldor.oblepiha_kotlin.view.ContentActivity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class AccrualsMembershipFragment : Fragment() {

    private var _binding: FragmentMembershipListBinding? = null
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
        _binding = FragmentMembershipListBinding.inflate(inflater, container, false)
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
        AccrualsMembershipViewModel.selectedForDetails.observe(viewLifecycleOwner, {
            Log.d("surprise", "AccrualsPowerFragment.kt 77: have goto action event $it")
            if (it != null) {
                // show power accrual list
                val navController = (requireActivity() as ContentActivity).navController
                if (navController.currentDestination?.id == R.id.navigation_accruals_membership) {
                    AccrualsMembershipDetailsViewModel.selectedForDetails = it
                    navController.navigate(R.id.action_show_membership_details)
                }
                AccrualsMembershipViewModel.selectedForDetails.removeObservers(viewLifecycleOwner)
                AccrualsMembershipViewModel.selectedForDetails.value = null
            }
        })
    }

    private fun handleRecycler() {
        // DataSource
        val dataSource = GetMembershipListDataSource()
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()
        val pageList: PagedList<MembershipListItem?> = PagedList.Builder(dataSource, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setNotifyExecutor(MainThreadExecutor())
            .build()
        recyclerView = root.findViewById(R.id.dataList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = MembershipListItemsAdapter(MembershipListItemDiffCallback())
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