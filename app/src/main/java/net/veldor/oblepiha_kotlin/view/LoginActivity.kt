package net.veldor.oblepiha_kotlin.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import net.veldor.oblepiha_kotlin.R
import net.veldor.oblepiha_kotlin.databinding.ActivityLoginBinding
import net.veldor.oblepiha_kotlin.model.view_models.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private var mConfirmExit: Long = 0
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private var sb: Snackbar? = null
    private lateinit var root: View
    var login = ""
    var password = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        Log.d("surprise", "LoginActivity.kt 29: i am here")
        binding = DataBindingUtil.inflate(
            layoutInflater, R.layout.activity_login, null, false
        )
        setContentView(binding.rootView)
        binding.handler = this
        root = binding.root
        viewModel =
            ViewModelProvider(this).get(LoginViewModel::class.java)
        setupUI()
    }

    override fun onResume() {
        super.onResume()
        setupObservers()
    }

    override fun onPause() {
        super.onPause()
        removeObservers()
    }

    private fun setupObservers() {
        viewModel.loginResult.observe(this, {
            if (it != null) {
                hideLoginInProgress()
                if (it.status.isNullOrEmpty()) {
                    Toast.makeText(this, getString(R.string.login_error_message), Toast.LENGTH_LONG)
                        .show()
                } else if (it.status == "failed") {
                    Toast.makeText(this, "Wrong login or password", Toast.LENGTH_LONG).show()
                } else if (it.status == "success") {
                    // redirect to main window
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        })
    }

    private fun removeObservers() {
        viewModel.loginResult.removeObservers(this)
    }

    private fun setupUI() {
        root.findViewById<TextInputEditText>(R.id.pass_edit_text)
            .setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login()
                    return@setOnEditorActionListener true
                }
                false
            }
    }

    fun login() {
        // hide software keyboard
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        // check login
        when {
            login.isEmpty() -> {
                root.findViewById<TextInputLayout>(R.id.name_text_input).error =
                    getString(R.string.login_required_message)
            }
            password.isEmpty() -> {
                root.findViewById<TextInputLayout>(R.id.name_text_input).error = null
                root.findViewById<TextInputLayout>(R.id.pass_text_input).error =
                    getString(R.string.pass_required_message)
            }
            else -> {
                root.findViewById<TextInputLayout>(R.id.name_text_input).error = null
                root.findViewById<TextInputLayout>(R.id.pass_text_input).error = null
                viewModel.login(login, password)
                showLoginInProgress()
            }
        }
    }

    private fun showLoginInProgress() {
        if (sb == null) {
            sb = Snackbar.make(this, root, "Logging in", Snackbar.LENGTH_INDEFINITE)
        }
        sb?.show()
    }

    private fun hideLoginInProgress() {
        sb?.dismiss()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

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
        return super.onKeyDown(keyCode, event)
    }
}