package net.veldor.oblepiha_kotlin.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.FragmentBillsBinding
import net.veldor.oblepiha_kotlin.model.delegates.BillInfoDelegate
import net.veldor.oblepiha_kotlin.model.selections.BillListItem
import net.veldor.oblepiha_kotlin.model.utils.TimeHandler
import net.veldor.oblepiha_kotlin.model.view_models.BillDetailsViewModel
import net.veldor.oblepiha_kotlin.model.view_models.BillsListViewModel
import net.veldor.oblepiha_kotlin.model.view_models.BillsViewModel
import net.veldor.oblepiha_kotlin.view.ContentActivity
import java.util.*

class BillsFragment : Fragment(), BillInfoDelegate {

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
        viewModel.state.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.payedBillsText.text = it.payed
                binding.unpayedBillsText.text = it.unpayed
                binding.unpayedBillsLoader.hideShimmer()
                binding.payedBillsLoader.hideShimmer()
            }
        }

        binding.updateTimeNotification.text =
            String.format(
                Locale.ENGLISH,
                getString(R.string.update_time_information_label),
                TimeHandler().timestampToDatetime(App.instance.preferences.getUpdateInfoTime())
            )

        if (requireActivity().intent.hasExtra("bill_id")) {
            Log.d("surprise", "BillsFragment.kt 56 onCreateView requiring bill info")
            // go to bill details
            viewModel.requireBillDetails(requireActivity().intent.getStringExtra("bill_id"), this)
        }
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

    fun showPayedBills() {
        BillsListViewModel.isClosed = true
        val navController = (requireActivity() as ContentActivity).navController
        if (navController.currentDestination?.id == R.id.navigation_bills) {
            navController.navigate(R.id.action_show_bills_list)
        }
    }

    fun showUnpayedBills() {
        BillsListViewModel.isClosed = false
        val navController = (requireActivity() as ContentActivity).navController
        if (navController.currentDestination?.id == R.id.navigation_bills) {
            navController.navigate(R.id.action_show_bills_list)
        }
    }

    override fun billInfoReceived(item: BillListItem?) {
        val navController = (requireActivity() as ContentActivity).navController
        BillDetailsViewModel.selectedForDetails = item
        navController.navigate(R.id.action_show_direct_bill_info)
    }
}