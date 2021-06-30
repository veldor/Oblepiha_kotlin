package net.veldor.oblepiha_kotlin.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import net.veldor.oblepiha_kotlin.App

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // if user is registered- go to main page, or show login page
        if(App.instance.preferences.isUserUnknown){
            Log.d("surprise", "MainActivity.kt 13: go to login")
            startActivity(Intent(this, LoginActivity::class.java))
        }
        else{
            startActivity(Intent(this, ContentActivity::class.java))
        }
    }
}