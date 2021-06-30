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
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.FragmentPowerBinding
import net.veldor.oblepiha_kotlin.model.adapters.PowerListAdapter
import net.veldor.oblepiha_kotlin.model.data_source.MyPositionalDataSource
import net.veldor.oblepiha_kotlin.model.data_source.PowerDataUtilCallback
import net.veldor.oblepiha_kotlin.model.database.entity.PowerData
import net.veldor.oblepiha_kotlin.model.view_models.PowerViewModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class PowerFragment : Fragment() {

    private lateinit var viewModel: PowerViewModel
    private var _binding: FragmentPowerBinding? = null
    private lateinit var recyclerView: RecyclerView

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

        _binding = FragmentPowerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // handle power data
        // DataSource
        val dataSource = MyPositionalDataSource(
            App.instance.database.powerDao()
        )
        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(10)
            .build()
        val pageList: PagedList<PowerData?> = PagedList.Builder(dataSource, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .setNotifyExecutor(MainThreadExecutor())
            .build()

        recyclerView = root.findViewById(R.id.dataList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = PowerListAdapter(PowerDataUtilCallback())
        adapter.submitList(pageList)
        recyclerView.adapter = adapter

        val spendForDay: TextView = binding.spendForDayData
        viewModel.spendForDay.observe(viewLifecycleOwner, {
            spendForDay.text = it
        })
        val spendForMonth: TextView = binding.spendForMonthData
        viewModel.spendForMonth.observe(viewLifecycleOwner, {
            spendForMonth.text = it
        })
        return root
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