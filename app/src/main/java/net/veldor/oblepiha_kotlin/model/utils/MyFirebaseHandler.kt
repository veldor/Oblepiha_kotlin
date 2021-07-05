package net.veldor.oblepiha_kotlin.model.utils

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import net.veldor.oblepiha_kotlin.App

class MyFirebaseHandler {
    // Get new FCM registration token
    // проверю наличие токена Firebase
    val token: Unit
        get() {
            // проверю наличие токена Firebase
            FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.d(
                            "surprise",
                            "Fetching FCM registration token failed",
                            task.exception
                        )
                        return@addOnCompleteListener
                    }
                    // Get new FCM registration token
                    val token: String? = task.getResult()
                    if (token != null) {
                        Log.d(
                            "surprise",
                            "FirebaseHandler getToken 24: save token $token"
                        )
                        FirebaseMessaging.getInstance().subscribeToTopic("news")
                            .addOnCompleteListener { result ->
                                Log.d("surprise", "have firebase topic subscription result")
                                if (!result.isSuccessful) {
                                    Log.d("surprise", "can't subscribe:")
                                } else {
                                    Log.d("surprise", "i subscribed for themes")
                                }
                            }
                        App.instance.preferences.firebaseToken = token
                    }
                }
        }
}