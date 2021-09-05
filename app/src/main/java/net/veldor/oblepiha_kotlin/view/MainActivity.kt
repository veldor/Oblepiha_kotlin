package net.veldor.oblepiha_kotlin.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import net.veldor.oblepiha_kotlin.App

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(App.instance.preferences.isUserUnknown){
            val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    startActivity(Intent(this, ContentActivity::class.java))
                }
            }
            val intent = Intent(this, LoginActivity::class.java)
            resultLauncher.launch(intent)
        }
        else{
            startActivity(Intent(this, ContentActivity::class.java))
        }
    }
}