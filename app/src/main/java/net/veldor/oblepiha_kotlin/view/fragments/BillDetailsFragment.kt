package net.veldor.oblepiha_kotlin.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.veldor.oblepiha_kotlin.databinding.FragmentBillDetailsBinding
import net.veldor.oblepiha_kotlin.model.adapters.BillEntitiesAdapter
import net.veldor.oblepiha_kotlin.model.adapters.BillTransactionsAdapter
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler
import net.veldor.oblepiha_kotlin.model.view_models.BillDetailsViewModel
import net.veldor.oblepiha_kotlin.view.ContentActivity
import net.veldor.oblepiha_kotlin.view.QrActivity

class BillDetailsFragment : Fragment() {

    private lateinit var viewModel: BillDetailsViewModel
    private var _binding: FragmentBillDetailsBinding? = null
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
            ViewModelProvider(this).get(BillDetailsViewModel::class.java)
        setHasOptionsMenu(true)
        _binding = FragmentBillDetailsBinding.inflate(inflater, container, false)
        binding.item = BillDetailsViewModel.selectedForDetails
        binding.accruedValue.text = GrammarHandler.showPrice(BillDetailsViewModel.selectedForDetails!!.totalSumm.toInt())
        binding.payedValue.text = GrammarHandler.showPrice(BillDetailsViewModel.selectedForDetails!!.payedSumm.toInt())
        binding.fromDepositValue.text = GrammarHandler.showPrice(BillDetailsViewModel.selectedForDetails!!.depositUsed.toInt())
        binding.toDepositValue.text = GrammarHandler.showPrice(BillDetailsViewModel.selectedForDetails!!.toDeposit.toInt())
        root = binding.root
        viewModel.loadAdditionalBillInfo()
        viewModel.billInfo.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it.entities.isNotEmpty()) {
                    // load recycler
                    binding.billContentList.layoutManager = LinearLayoutManager(requireContext())
                    binding.billContentList.adapter = BillEntitiesAdapter(it.entities)
                }
                if(it.transactions.isNotEmpty()){
                    binding.billPaysList.layoutManager = LinearLayoutManager(requireContext())
                    binding.billPaysList.adapter = BillTransactionsAdapter(it.transactions)
                }
                binding.contentLoader.hideShimmer()
            }
        })
        binding.loadQrButton.setOnClickListener {
            val intent = Intent(requireContext(), QrActivity::class.java)
            intent.putExtra(QrActivity.BILL_ID, BillDetailsViewModel.selectedForDetails!!.id)
            startActivity(intent)
        }
        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (requireActivity() as ContentActivity).navController.navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }
}