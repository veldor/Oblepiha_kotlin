package net.veldor.oblepiha_kotlin.view.fragments

import android.Manifest
import android.Manifest.permission.CALL_PHONE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.FragmentDefenceBinding
import net.veldor.oblepiha_kotlin.model.utils.GatesHandler
import net.veldor.oblepiha_kotlin.model.utils.GrammarHandler
import net.veldor.oblepiha_kotlin.model.utils.RawDataHandler
import net.veldor.oblepiha_kotlin.model.utils.SuburbanHandler
import net.veldor.oblepiha_kotlin.model.view_models.DefenceViewModel
import net.veldor.oblepiha_kotlin.model.view_models.SuburbansViewModel
import net.veldor.oblepiha_kotlin.view.ContentActivity
import net.veldor.oblepiha_kotlin.view.REQUEST_CALL
import java.util.*


class DefenceStateFragment : Fragment() {

    lateinit var mainHandler: Handler

    private val updateTextTask = object : Runnable {
        override fun run() {
            Log.d("surprise", "run: tick")
            var info = App.instance.incomingSuburbans.value
            if (info != null) {
                val next = SuburbanHandler(info).getNext()
                binding.fromCityTrainTime.text = next
            }
            info = App.instance.outgoingSuburbans.value
            if (info != null) {
                val next = SuburbanHandler(info).getNext()
                binding.toCityTrainTime.text = next
            }
            mainHandler.postDelayed(this, 60000)
        }
    }

    var personName = "--"
    var cottageNumber = "--"
    var externalTemperature = "0"
    var powerData = "0"
    var debt = "0"
    var openedBillsState = "Счета оплачены"
    private var waitingDialog: AlertDialog? = null

    private lateinit var viewModel: DefenceViewModel
    private var _binding: FragmentDefenceBinding? = null
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
            ViewModelProvider(this).get(DefenceViewModel::class.java)
        // fill by last received data
        _binding = FragmentDefenceBinding.inflate(inflater, container, false)
        binding.handler = this
        val root: View = binding.root
        swipeRefreshLayout = root.findViewById(R.id.swipeLayout)
        // handle refresh layout
        swipeRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light
        )
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.checkStatus()
        }
        mainHandler = Handler(Looper.getMainLooper())
        return root
    }

    override fun onResume() {
        super.onResume()
        setupObservers()
        viewModel.checkStatus()

        mainHandler.post(updateTextTask)
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateTextTask)
    }

    private fun setupObservers() {
        App.instance.mCurrentStatusResponse.observe(requireActivity(), {
            Log.d("surprise", "setupObservers: have state update!")
            swipeRefreshLayout.isRefreshing = false
            // если проверка неудачна и токен не подходит- переброшу на активити логина
            if (it.status == "failed" && it.message == "wrong token") {
                App.instance.preferences.saveToken(null)
                requireActivity().finish()
                Toast.makeText(requireContext(), "Auth error, need to re-log", Toast.LENGTH_SHORT)
                    .show()
                return@observe
            }

            if (it.have_defence == 0) {
                binding.defenceStateCard.visibility = View.GONE
            }


            binding.cottageNumberText.text =
                String.format(Locale.ENGLISH, "Участок %s", it.cottage_number)
            binding.cottageOwnerText.text = it.owner_io
            personName = it.owner_io
            cottageNumber = it.cottage_number
            val encodedTemp = GrammarHandler.handleTemperature(it.temp)
            externalTemperature = String.format(
                Locale.ENGLISH, getString(R.string.temp_value), encodedTemp
            )
            if (encodedTemp >= 0) {
                binding.currentTemp.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_temp_plus,
                    0,
                    0,
                    0
                )
            } else {

                binding.currentTemp.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_temp_minus,
                    0,
                    0,
                    0
                )
            }
            if (it.raw_data.isNotEmpty() && it.initial_value.isNotEmpty() && it.channel > 0) {
                try {
                    // посчитаю расход
                    val handler = RawDataHandler(it.raw_data)
                    val used: String = handler.getUsed(it.channel, it.initial_value)
                    powerData = java.lang.String.format(
                        Locale.ENGLISH,
                        getString(R.string.title_power_data),
                        used
                    )
                } catch (ignored: Exception) {
                }
            } else {
                powerData = java.lang.String.format(
                    Locale.ENGLISH,
                    getString(R.string.title_power_data),
                    GrammarHandler.handleWatt(it.last_data)
                )
            }
            // check debt
            if (it.total_duty > 0) {
                debt = "-" + GrammarHandler.showPrice(it.total_duty)
                //  change color
                binding.currentDuty.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.text_warning,
                        null
                    )
                )
            } else {
                debt = "Долгов нет"
            }
            // check bills
            if (it.opened_bills.toInt() > 0) {
                openedBillsState = "Есть неоплаченные счета"
                //  change color
                binding.openedBills.setTextColor(
                    ResourcesCompat.getColor(
                        resources,
                        R.color.text_warning,
                        null
                    )
                )
            } else {
                openedBillsState = "Счета оплачены"
            }
            if (it.perimeter_state.isNotEmpty()) {
                if (it.perimeter_state == "замкнут") {
                    binding.contactStatus.setTextColor(Color.parseColor("#FF8BC34A"))
                    binding.contactStatus.text = getString(R.string.contact_locked_message)
                    binding.contactStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_baseline_lock_24,
                        0,
                        0,
                        0
                    )
                } else if (it.perimeter_state == "разомкнут") {
                    binding.contactStatus.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.text_warning,
                            null
                        )
                    )
                    binding.contactStatus.text = getString(R.string.contact_unlocked_message)
                    binding.contactStatus.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_baseline_lock_open_24,
                        0,
                        0,
                        0
                    )
                }
            }
            if (it.current_status) {
                setAlertEnabled()
            } else {
                setAlertDisabled()
            }
            binding.invalidateAll()
            binding.notifyChange()
        })
        viewModel.actionInProgress.observe(requireActivity(), {
            if (it) {
                showLoadingDialog()
            } else {
                hideLoadingDialog()
            }
        })
        viewModel.statusChangeRequestAccepted.observe(requireActivity(), {
            Log.d(
                "surprise",
                "setupObservers: =========================STATE CHANGE REQUEST ACCEPTED================================="
            )
            if (it) {
                // запрошу текущий статус
                viewModel.checkStatus()
                // статус защиты успешно изменён
                Toast.makeText(requireContext(), "Defense status changed", Toast.LENGTH_SHORT)
                    .show()
                hideLoadingDialog()
            } else {
                // запрошу текущий статус
                viewModel.checkStatus()
                Toast.makeText(
                    requireContext(),
                    "Can't change status, please, try later",
                    Toast.LENGTH_SHORT
                ).show()
                hideLoadingDialog()
            }
        })

        App.instance.incomingSuburbans.observe(this, {
            val next = SuburbanHandler(it).getNext()
            binding.fromCityTrainTime.text = next
        })

        App.instance.outgoingSuburbans.observe(this, {
            val next = SuburbanHandler(it).getNext()
            binding.toCityTrainTime.text = next
        })
    }

    private fun setAlertDisabled() {
        binding.defenceState.isChecked = false
        binding.defenceState.isEnabled = true
        binding.defenceState.text = getString(R.string.message_alert_disabled)
        binding.defenceState.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_defence_disabled,
            0,
            0,
            0
        )
        binding.defenceState.setTextColor(Color.parseColor("#FFFF5722"))
        binding.defenceState.setOnClickListener {
            Log.d("surprise", "onCreateView: enable defence")
            Toast.makeText(
                requireContext(),
                getString(R.string.message_enabling_alert),
                Toast.LENGTH_SHORT
            ).show()
            viewModel.switchAlertMode(true)
        }
    }

    private fun setAlertEnabled() {
        binding.defenceState.isChecked = true
        binding.defenceState.text = getString(R.string.message_alert_enabled)
        binding.defenceState.isEnabled = true
        binding.defenceState.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_defence_enabled,
            0,
            0,
            0
        )
        binding.defenceState.setTextColor(Color.parseColor("#FF8BC34A"))
        binding.defenceState.setOnClickListener {
            Log.d("surprise", "onCreateView: disable defence")
            Toast.makeText(
                requireContext(),
                getString(R.string.message_disabling_alert),
                Toast.LENGTH_SHORT
            ).show()
            viewModel.switchAlertMode(false)
        }
    }

    private fun showLoadingDialog() {
        if (waitingDialog == null) {
            // создам диалоговое окно
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setTitle(R.string.waiting_dialog_title)
                .setView(R.layout.loading_dialog_layout)
                .setCancelable(false)
            waitingDialog = dialogBuilder.create()
        }
        waitingDialog?.show()
    }

    private fun hideLoadingDialog() {
        waitingDialog?.hide()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        removeObservers()
    }

    private fun removeObservers() {
        App.instance.mCurrentStatusResponse.removeObservers(requireActivity())
        viewModel.actionInProgress.removeObservers(requireActivity())
        viewModel.statusChangeRequestAccepted.removeObservers(requireActivity())
    }

    fun logout() {
        // создам диалоговое окно
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(getString(R.string.logout_confirm_title))
            .setMessage(getString(R.string.logout_dialog_body_message))
            .setCancelable(true)
            .setPositiveButton(
                R.string.ok
            ) { _: DialogInterface?, _: Int ->
                // удалю токен и запущу окно аутентификации
                viewModel.logout()
                App.instance.preferences.saveToken(null)
                requireActivity().finish()
            }
            .setNegativeButton(R.string.cancel, null)
        dialogBuilder.show()
    }

    fun showTooltip(view: View) {
        when (view.id) {
            R.id.current_temp -> Toast.makeText(
                requireContext(),
                "Температура на улице",
                Toast.LENGTH_SHORT
            ).show()
            R.id.current_power_data -> Toast.makeText(
                requireContext(),
                "Последние показания счётчика",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun showDuties(view: View) {
        // go to duties tab
        val navController = (requireActivity() as ContentActivity).navController
        if (navController.currentDestination?.id == R.id.navigation_defence_state) {
            navController.navigate(R.id.navigation_accruals)
        }
    }

    fun showBills(view: View) {
        // go to duties tab
        val navController = (requireActivity() as ContentActivity).navController
        if (navController.currentDestination?.id == R.id.navigation_defence_state) {
            navController.navigate(R.id.navigation_bills)
        }
    }

    fun showSuburbans(view: View) {
        // check schedule loaded
        if (App.instance.incomingSuburbans.value != null) {
            when (view.id) {
                R.id.trainToCityContainer -> {
                    val navController = (requireActivity() as ContentActivity).navController
                    if (navController.currentDestination?.id == R.id.navigation_defence_state) {
                        SuburbansViewModel.destination = "out"
                        navController.navigate(R.id.action_show_suburbans)
                    }
                }
                R.id.trainFromCityContainer -> {
                    val navController = (requireActivity() as ContentActivity).navController
                    if (navController.currentDestination?.id == R.id.navigation_defence_state) {
                        SuburbansViewModel.destination = "in"
                        navController.navigate(R.id.action_show_suburbans)
                    }
                }
            }
        }
    }

    fun openGates(view: View) {
        // if granted permission to call
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val callPermission =
                requireActivity().checkSelfPermission(CALL_PHONE)
            if (callPermission == PackageManager.PERMISSION_GRANTED) {
                GatesHandler().openGates()
            }
            else{
                val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.dialogTheme)
                dialogBuilder.setTitle(R.string.permissions_dialog_title)
                    .setMessage("Для открытия ворот необходимо совершить звонок")
                    .setCancelable(false)
                    .setPositiveButton(
                        R.string.permissions_dialog_positive_answer
                    ) { _, _ ->
                        androidx.core.app.ActivityCompat.requestPermissions(
                            requireActivity(), arrayOf(
                                CALL_PHONE,
                            )
                            , REQUEST_CALL
                        )
                    }
                    .setNegativeButton(
                        R.string.permissions_dialog_negative_answer
                    ) { _, _ ->
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.file_permissons_not_granted),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                dialogBuilder.create().show()
            }
        } else {
            GatesHandler().openGates()
        }
    }
}