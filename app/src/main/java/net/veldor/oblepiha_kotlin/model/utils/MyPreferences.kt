package net.veldor.oblepiha_kotlin.model.utils

import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.google.gson.Gson
import net.veldor.oblepiha_kotlin.App
import net.veldor.oblepiha_kotlin.model.selections.ApiCurrentStatusResponse


class MyPreferences {
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
        mSharedPreferences.edit().putInt(PREFERENCE_UPDATE_INFO_TIME, response.update_info_time)
            .apply()
        mSharedPreferences.edit().putString(PREFERENCE_LAST_STATE, json).apply()
    }

    fun isShowUsedPower(): Boolean {
        return mSharedPreferences.getBoolean(PREFERENCE_SHOW_USED_POWER, false)
    }

    fun getUpdateInfoTime(): Int {
        return mSharedPreferences.getInt(PREFERENCE_UPDATE_INFO_TIME, 0)

    }

    fun setShowUsedPower(state: Boolean) {
        Log.d("surprise", "MyPreferences.kt 50 setShowUsedPower change used power $state")
        mSharedPreferences.edit().putBoolean(PREFERENCE_SHOW_USED_POWER, state).apply()

    }

    fun setShowDataTransfers(state: Boolean) {
        mSharedPreferences.edit().putBoolean(PREFERENCE_SHOW_DATA_TRANSFERS, state).apply()

    }

    fun isShowDataTransfers(): Boolean {
        return mSharedPreferences.getBoolean(PREFERENCE_SHOW_DATA_TRANSFERS, false)
    }

    val lastCheckTime: Long
        get() = mSharedPreferences.getLong(PREFERENCE_LAST_CHECK, 0)

    companion object {
        private const val AUTH_TOKEN = "auth token"
        private const val PREFERENCE_DEFENCE_STATE = "defence state"
        private const val PREFERENCE_FIREBASE_TOKEN = "firebase token"
        private const val PREFERENCE_LAST_CHECK = "last check"
        private const val PREFERENCE_LAST_STATE = "last state"
        private const val PREFERENCE_SHOW_USED_POWER = "show used power"
        private const val PREFERENCE_UPDATE_INFO_TIME = "update info time"
        private const val PREFERENCE_SHOW_DATA_TRANSFERS = "show data tranfers"
    }

    init {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(App.instance)
    }
}