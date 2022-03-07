package net.veldor.oblepiha_kotlin.view.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.FragmentBillDetailsBinding
import net.veldor.oblepiha_kotlin.model.adapters.BillEntitiesAdapter
import net.veldor.oblepiha_kotlin.model.adapters.BillTransactionsAdapter
import net.veldor.oblepiha_kotlin.model.utils.FileHandler
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

        updateInvoiceInfo()

        binding.downloadFileBtn.setOnClickListener {
            viewModel.loadInvoice()
            binding.downloadFileBtn.visibility = View.GONE
            binding.contentLoadingProgressBar.visibility = View.VISIBLE
        }

        binding.openFileBtn.setOnClickListener {
            viewModel.openInvoice()
        }

        binding.menuBtn.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                it.showContextMenu(it.pivotX, it.pivotY)
            }
            else{
                it.showContextMenu()
            }
        }
        binding.menuBtn.setOnCreateContextMenuListener { menu, v, menuInfo ->
            var shareMenuItem: MenuItem = menu.add(App.instance.getString(R.string.share_file_menu_item))
            shareMenuItem.setOnMenuItemClickListener {
                viewModel.shareInvoice()
                true
            }
            shareMenuItem = menu.add(App.instance.getString(R.string.re_download_file))
            shareMenuItem.setOnMenuItemClickListener {
                binding.contentLoadingProgressBar.visibility = View.VISIBLE
                binding.openFileBtn.visibility = View.GONE
                binding.menuBtn.visibility = View.GONE
                viewModel.downloadFileAgain()
                true
            }
        }

        root = binding.root
        viewModel.loadAdditionalBillInfo()
        BillDetailsViewModel.someFileLoadLiveData.observe(viewLifecycleOwner) {
            updateInvoiceInfo()
        }
        viewModel.billInfo.observe(viewLifecycleOwner) {
            if (it != null) {
                if (it.entities.isNotEmpty()) {
                    // load recycler
                    binding.billContentList.layoutManager = LinearLayoutManager(requireContext())
                    binding.billContentList.adapter = BillEntitiesAdapter(it.entities)
                }
                if (it.transactions.isNotEmpty()) {
                    binding.billPaysList.layoutManager = LinearLayoutManager(requireContext())
                    binding.billPaysList.adapter = BillTransactionsAdapter(it.transactions)
                }
                binding.contentLoader.hideShimmer()
            }
        }
        binding.loadQrButton.setOnClickListener {
            val intent = Intent(requireContext(), QrActivity::class.java)
            intent.putExtra(QrActivity.BILL_ID, BillDetailsViewModel.selectedForDetails!!.id)
            startActivity(intent)
        }
        return root
    }

    private fun updateInvoiceInfo() {
        if(FileHandler().fileExists(BillDetailsViewModel.selectedForDetails!!.id.toString() + ".pdf")){
            // bill loaded
            binding.downloadFileBtn.visibility = View.GONE
            binding.openFileBtn.visibility = View.VISIBLE
            binding.contentLoadingProgressBar.visibility = View.GONE
            binding.menuBtn.visibility = View.VISIBLE
        }
        else{
            binding.downloadFileBtn.visibility = View.VISIBLE
            binding.openFileBtn.visibility = View.GONE
            binding.contentLoadingProgressBar.visibility = View.GONE
            binding.menuBtn.visibility = View.GONE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (requireActivity() as ContentActivity).navController.navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }
}