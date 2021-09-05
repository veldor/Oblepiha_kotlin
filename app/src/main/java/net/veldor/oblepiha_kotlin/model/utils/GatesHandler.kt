package net.veldor.oblepiha_kotlin.model.utils

import android.content.Intent
import android.net.Uri
import net.veldor.oblepiha_kotlin.App

class GatesHandler {
    public fun openGates() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+79524599194"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        App.Companion.instance.applicationContext.startActivity(intent)
    }
}