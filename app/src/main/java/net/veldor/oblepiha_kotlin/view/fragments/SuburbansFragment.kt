package net.veldor.oblepiha_kotlin.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.databinding.FragmentSuburbansBinding
import net.veldor.oblepiha_kotlin.model.adapters.SuburbansAdapter
import net.veldor.oblepiha_kotlin.model.view_models.SuburbansViewModel
import net.veldor.oblepiha_kotlin.view.ContentActivity

class SuburbansFragment : Fragment() {

    private lateinit var viewModel: SuburbansViewModel
    private var _binding: FragmentSuburbansBinding? = null

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
            ViewModelProvider(this).get(SuburbansViewModel::class.java)
        _binding = FragmentSuburbansBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.dataList.adapter =
            SuburbansAdapter(if (SuburbansViewModel.destination == "out") App.instance.outgoingSuburbans.value!! else App.instance.incomingSuburbans.value!!)
        binding.dataList.layoutManager = LinearLayoutManager(requireContext())
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            (requireActivity() as ContentActivity).navController.navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }
}