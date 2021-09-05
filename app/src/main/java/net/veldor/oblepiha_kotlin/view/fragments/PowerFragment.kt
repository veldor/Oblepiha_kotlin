package net.veldor.oblepiha_kotlin.view.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.FragmentPowerBinding
import net.veldor.oblepiha_kotlin.model.adapters.PowerListAdapter
import net.veldor.oblepiha_kotlin.model.view_models.PowerViewModel
import java.util.concurrent.Executor

class PowerFragment : Fragment() {

    private lateinit var viewModel: PowerViewModel
    private var _binding: FragmentPowerBinding? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this).get(PowerViewModel::class.java)
        viewModel.requestData()
        _binding = FragmentPowerBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = root.findViewById(R.id.dataList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PowerListAdapter(listOf())
        recyclerView.adapter = adapter

        val spendForDay: TextView = binding.dayUseText
        viewModel.spendForDay.observe(viewLifecycleOwner, {
            spendForDay.text = it
        })
        val spendForMonth: TextView = binding.monthUseText
        viewModel.spendForMonth.observe(viewLifecycleOwner, {
            spendForMonth.text = it
        })

        binding.calendarNextButton.setOnClickListener {
            viewModel.loadNextMonth()
        }
        binding.calendarPrevButton.setOnClickListener {
            viewModel.loadPrevMonth()
        }
        swipeRefreshLayout = binding.swipeLayout
        // handle refresh layout
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.requestData()
        }
        binding.dateDisplayToday.setOnClickListener {
            viewModel.requestCurrentData()
        }
        setupObservers()
        return root
    }

    private fun setupObservers() {
        viewModel.month.observe(viewLifecycleOwner, {
            binding.dateCurrentMonth.text = it
        })
        viewModel.year.observe(viewLifecycleOwner, {
            binding.dateDisplayYear.text = it.toString()
        })
        viewModel.list.observe(viewLifecycleOwner, {
            (recyclerView.adapter as PowerListAdapter).setItems(it)
            swipeRefreshLayout.isRefreshing = false
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal class MainThreadExecutor : Executor {
        private val handler: Handler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            handler.post(command)
        }
    }
}