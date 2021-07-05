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
import net.veldor.oblepiha_kotlin.databinding.*
import net.veldor.oblepiha_kotlin.model.adapters.PaysAdapter
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

class AccrualsMembershipDetailsFragment : Fragment() {


    private lateinit var viewModel: AccrualsMembershipDetailsViewModel
    private var _binding: FragmentMembershipDetailsBinding? = null
    private lateinit var root: View

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this).get(AccrualsMembershipDetailsViewModel::class.java)
        setHasOptionsMenu(true)
        _binding = FragmentMembershipDetailsBinding.inflate(inflater, container, false)
        binding.item = AccrualsMembershipDetailsViewModel.selectedForDetails
        root = binding.root
        viewModel.requestPays()
        viewModel.paysInfo.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.paysList.layoutManager = LinearLayoutManager(requireContext())
                binding.paysList.adapter = PaysAdapter(it.list)
                binding.paysLoader.hideShimmer()
            }
        })
        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (requireActivity() as ContentActivity).navController.navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }
}