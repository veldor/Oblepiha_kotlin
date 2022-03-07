package net.veldor.oblepiha_kotlin.view.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.model.view_models.PreferencesViewModel
import net.veldor.oblepiha_kotlin.view.MainActivity

class PreferencesFragment : PreferenceFragmentCompat() {

    private lateinit var viewModel: PreferencesViewModel

    private var backupLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val treeUri = data!!.data
                if (treeUri != null) {
                    // проверю наличие файла
                    val dl = DocumentFile.fromTreeUri(requireContext(), treeUri)
                    if (dl != null && dl.isDirectory) {
                        viewModel.createBackup(dl)
                    }
                }
            }
        }
    private var restoreLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val treeUri = data!!.data
                if (treeUri != null) {
                    // проверю наличие файла
                    val dl = DocumentFile.fromSingleUri(requireContext(), treeUri)
                    if (dl != null && dl.isFile) {
                        viewModel.restoreBackup(dl)
                    }
                }
            }
        }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        val rootScreen = preferenceManager.createPreferenceScreen(
            requireContext()
        )
        viewModel =
            ViewModelProvider(this)[PreferencesViewModel::class.java]
        preferenceScreen = rootScreen

        val backupPref = Preference(requireContext())
        backupPref.title = getString(R.string.backup_title)
        backupPref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            // select folder for save
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.select_backup_folder_pref),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                            or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                )
                backupLauncher.launch(intent)
            }
            true
        }
        rootScreen.addPreference(backupPref)

        val restorePref = Preference(requireContext())
        restorePref.title = getString(R.string.restore_title)
        restorePref.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            // select folder for save
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.select_backup_file_message),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.type = "*/*"
                intent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                            or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                )
                restoreLauncher.launch(intent)
            }
            true
        }
        rootScreen.addPreference(restorePref)

        val notifyPowerUseSwitcher = SwitchPreferenceCompat(requireContext())
        notifyPowerUseSwitcher.isChecked = App.instance.preferences.isShowUsedPower()
        notifyPowerUseSwitcher.switchTextOff = "Отчёты о потраченной электроэнергии не отображаются"
        notifyPowerUseSwitcher.switchTextOn = "Отчёты о потраченной электроэнергии отображаются"
        notifyPowerUseSwitcher.title = "Отчёты о потраченной электроэнергии"
        notifyPowerUseSwitcher.setOnPreferenceChangeListener { _, value ->
            App.instance.preferences.setShowUsedPower(value as Boolean)
            viewModel.notifyPowerUseShowStateChanged(value)
            return@setOnPreferenceChangeListener true
        }
        rootScreen.addPreference(notifyPowerUseSwitcher)
        val logoutPreference = Preference(requireContext())
        logoutPreference.title = "Выйти из учётной записи"
        logoutPreference.setOnPreferenceClickListener {
            AlertDialog.Builder(activity)
                .setTitle("Выход из учётной записи")
                .setMessage("Выйти из учётной записи? Все данные, сохранённые на устройстве, будут стёрты.")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Выйти") { _, _ ->
                    // logout
                    App.instance.preferences.saveToken(null)
                    startActivity(Intent(context, MainActivity::class.java))
                }
                .show()
            return@setOnPreferenceClickListener true
        }
        rootScreen.addPreference(logoutPreference)
    }
}