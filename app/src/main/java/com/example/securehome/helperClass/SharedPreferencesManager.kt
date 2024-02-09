package com.example.securehome.helperClass

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences:SharedPreferences=context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)

    companion object{
        private const val PREFS_NAME="secure_home_shared_preference"
        private const val USER_ID_KEY="userId"
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(USER_ID_KEY, userId).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(USER_ID_KEY, null)
    }

    fun clearUserId() {
        sharedPreferences.edit().remove(USER_ID_KEY).apply()
    }
}