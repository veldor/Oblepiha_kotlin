package net.veldor.oblepiha_kotlin.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.veldor.oblepiha_kotlin.databinding.FragmentPowerDetailsBinding
import net.veldor.oblepiha_kotlin.model.adapters.PaysAdapter
import net.veldor.oblepiha_kotlin.model.view_models.AccrualsPowerDetailsViewModel
import net.veldor.oblepiha_kotlin.view.ContentActivity

class AccrualsPowerDetailsFragment : Fragment() {

    private lateinit var viewModel: AccrualsPowerDetailsViewModel
    private var _binding: FragmentPowerDetailsBinding? = null
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
        viewModel =
            ViewModelProvider(this).get(AccrualsPowerDetailsViewModel::class.java)
        _binding = FragmentPowerDetailsBinding.inflate(inflater, container, false)
        binding.item = AccrualsPowerDetailsViewModel.selectedForDetails
        root = binding.root
        viewModel.requestPays()
        viewModel.paysInfo.observe(viewLifecycleOwner, {
            if (it != null) {
                Log.d("surprise", "AccrualsPowerDetailsFragment.kt 38: setup adapter")
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