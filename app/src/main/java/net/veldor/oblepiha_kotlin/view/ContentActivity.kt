package net.veldor.oblepiha_kotlin.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.ActivityContentBinding
import net.veldor.oblepiha_kotlin.model.utils.GatesHandler

const val REQUEST_WRITE_READ = 3
const val REQUEST_CALL = 4

class ContentActivity : AppCompatActivity() {

    private var mConfirmExit: Long = 0
    private lateinit var binding: ActivityContentBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        // ask to disable dose mode
        checkDose()
        binding = ActivityContentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkFileAccess()
        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_content)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_defence_state,
                R.id.navigation_power_information,
                R.id.navigation_accruals,
                R.id.navigation_bills,
                R.id.navigation_preferences
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        App.instance.connectionError.observe(this) {
            if (it!!) {
                val snackbar = Snackbar.make(
                    binding.root,
                    getString(R.string.no_connection_title),
                    Snackbar.LENGTH_INDEFINITE
                )
                snackbar.setAction(getString(R.string.retry_title)) {
                    // reload fragment in nav
                    refreshCurrentFragment()
                }
                snackbar.anchorView = navView
                snackbar.show()
            }
        }

        if (intent.hasExtra("type")) {
            val action = intent.getStringExtra("type")
            if (action != null) {
                if (action == "power_data") {
                    binding.navView.selectedItemId = R.id.navigation_power_information
                } else if (action == "new_bill" || action == "pay_confirm") {
                    binding.navView.selectedItemId = R.id.navigation_bills
                }
            }
        }
        else if(intent.hasExtra("bill_id")){
            Log.d("surprise", "ContentActivity.kt 92 onCreate have bill id for open")
            binding.navView.selectedItemId = R.id.navigation_bills
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (navController.currentDestination?.id == R.id.navigation_defence_state) {
                if (mConfirmExit != 0L) {
                    if (mConfirmExit > System.currentTimeMillis() - 3000) {
                        // выйду из приложения
                        Log.d("surprise", "OPDSActivity onKeyDown exit")
                        // this.finishAffinity();
                        val startMain = Intent(Intent.ACTION_MAIN)
                        startMain.addCategory(Intent.CATEGORY_HOME)
                        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(startMain)
                    } else {
                        Toast.makeText(
                            this,
                            "Нажмите ещё раз для выхода",
                            Toast.LENGTH_SHORT
                        ).show()
                        mConfirmExit = System.currentTimeMillis()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Нажмите ещё раз для выхода",
                        Toast.LENGTH_SHORT
                    ).show()
                    mConfirmExit = System.currentTimeMillis()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun refreshCurrentFragment() {
        val id = navController.currentDestination?.id
        navController.popBackStack(id!!, true)
        navController.navigate(id)
    }

    private fun checkDose() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = packageName
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                showDisableDoseDialog()
            }
        }
    }

    @SuppressLint("BatteryLife")
    private fun showDisableDoseDialog() {
        // создам диалоговое окно
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(R.string.disable_dose_dialog_title)
            .setMessage(getString(R.string.disable_dose_dialog_message))
            .setCancelable(true)
            .setPositiveButton(
                android.R.string.ok
            ) { _: DialogInterface?, _: Int ->
                val intent = Intent()
                val packageName = packageName
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                }
            }
        dialogBuilder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_WRITE_READ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val writeResult = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                val readResult: Int =
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                if (writeResult != PackageManager.PERMISSION_GRANTED || readResult != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        this,
                        getString(R.string.file_permissons_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } else if (requestCode == REQUEST_CALL) {
            Log.d("surprise", "onActivityResult: permission granted, make a call")
            GatesHandler().openGates()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}

private fun ContentActivity.checkFileAccess() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val writeResult = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readResult: Int =
            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (writeResult != PackageManager.PERMISSION_GRANTED || readResult != PackageManager.PERMISSION_GRANTED) {
            val dialogBuilder = AlertDialog.Builder(this, R.style.dialogTheme)
            dialogBuilder.setTitle(R.string.permissions_dialog_title)
                .setMessage("Для загрузки файлов необходимо предоставить доступ к памяти устройства")
                .setCancelable(false)
                .setPositiveButton(
                    R.string.permissions_dialog_positive_answer
                ) { _, _ ->
                    androidx.core.app.ActivityCompat.requestPermissions(
                        this, arrayOf(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ), REQUEST_WRITE_READ
                    )
                }
                .setNegativeButton(
                    R.string.permissions_dialog_negative_answer
                ) { _, _ ->
                    Toast.makeText(
                        this,
                        getString(R.string.file_permissons_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            dialogBuilder.create().show()
        }
    }

}