package net.veldor.oblepiha_kotlin.model.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.gson.Gson
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.selections.ApiCurrentStatusResponse


class MyPreferences{
    val mSharedPreferences: SharedPreferences
    val isUserUnknown: Boolean
        get() = mSharedPreferences.getString(AUTH_TOKEN, null) == null

    fun saveToken(token: String?) {
        mSharedPreferences.edit().putString(AUTH_TOKEN, token).apply()
    }

    val token: String?
        get() = mSharedPreferences.getString(AUTH_TOKEN, null)

    val lastDefenceState: Boolean
        get() = mSharedPreferences.getBoolean(PREFERENCE_DEFENCE_STATE, false)

    fun setDefenceState(state: Boolean) {
        mSharedPreferences.edit().putBoolean(PREFERENCE_DEFENCE_STATE, state).apply()
    }

    var firebaseToken: String?
        get() = mSharedPreferences.getString(PREFERENCE_FIREBASE_TOKEN, null)
        set(token) {
            mSharedPreferences.edit().putString(PREFERENCE_FIREBASE_TOKEN, token).apply()
        }

    fun saveLastCheckTime() {
        mSharedPreferences.edit().putLong(PREFERENCE_LAST_CHECK, System.currentTimeMillis()).apply()
    }

    fun saveLastResponse(response: ApiCurrentStatusResponse) {
        val gson = Gson()
        val json = gson.toJson(response)
        mSharedPreferences.edit().putString(PREFERENCE_LAST_STATE, json).apply()
    }

    val lastCheckTime: Long
        get() = mSharedPreferences.getLong(PREFERENCE_LAST_CHECK, 0)

    companion object {
        private const val AUTH_TOKEN = "auth token"
        private const val PREFERENCE_DEFENCE_STATE = "defence state"
        private const val PREFERENCE_FIREBASE_TOKEN = "firebase token"
        private const val PREFERENCE_LAST_CHECK = "last check"
        private const val PREFERENCE_LAST_STATE = "last state"
    }

    init {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.instance)
    }
}