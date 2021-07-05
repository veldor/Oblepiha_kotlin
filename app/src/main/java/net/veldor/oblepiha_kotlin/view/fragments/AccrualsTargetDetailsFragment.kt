package net.veldor.oblepiha_kotlin.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.veldor.oblepiha_kotlin.databinding.FragmentTargetDetailsBinding
import net.veldor.oblepiha_kotlin.model.adapters.PaysAdapter
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsMembershipDetailsViewModel
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsTargetDetailsViewModel
import net.veldor.oblepiha_kotlin.view.ContentActivity

class AccrualsTargetDetailsFragment : Fragment() {

    private lateinit var viewModel: AccrualsTargetDetailsViewModel
    private var _binding: FragmentTargetDetailsBinding? = null
    private lateinit var root: View

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentTargetDetailsBinding.inflate(inflater, container, false)
        binding.item = AccrualsTargetDetailsViewModel.selectedForDetails
        viewModel =
            ViewModelProvider(this).get(AccrualsTargetDetailsViewModel::class.java)
        viewModel.requestPays()
        viewModel.paysInfo.observe(viewLifecycleOwner, {
            if (it != null) {
                binding.paysList.layoutManager = LinearLayoutManager(requireContext())
                binding.paysList.adapter = PaysAdapter(it.list)
                binding.paysLoader.hideShimmer()
            }
        })
        root = binding.root
        return root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (requireActivity() as ContentActivity).navController.navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }
}