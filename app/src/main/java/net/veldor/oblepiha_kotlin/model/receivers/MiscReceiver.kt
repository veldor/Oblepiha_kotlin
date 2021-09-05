package net.veldor.oblepiha_kotlin.model.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import net.veldor.oblepiha_kotlin.model.utils.GatesHandler

class MiscReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("surprise", "onReceive: have broadcast")
        val type = intent.getStringExtra(EXTRA_ACTION)
        if(type.equals(ACTION_OPEN_GATES)){
            Log.d("surprise", "onReceive: start open gates")
            GatesHandler().openGates()
        }
    }

    companion object {
        const val EXTRA_ACTION = "action"
        const val ACTION_OPEN_GATES = "open gates"
    }
}