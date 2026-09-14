package com.example.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.data.api.ApiClient
import com.example.data.api.UnifiedSubscription

class SessionManager(context: Context) {

    private val appContext = context.applicationContext
    private var prefs: SharedPreferences = createPreferences()

    private fun createPreferences(): SharedPreferences {
        return try {
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            EncryptedSharedPreferences.create(
                "skyline_secure_session",
                masterKeyAlias,
                appContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (_: Exception) {
            // Fallback for devices or emulators with keystore anomalies
            appContext.getSharedPreferences("skyline_session_fallback", Context.MODE_PRIVATE)
        }
    }

    private fun safeGet(action: (SharedPreferences) -> String?): String? {
        return try {
            action(prefs)
        } catch (_: Exception) {
            prefs = appContext.getSharedPreferences("skyline_session_fallback", Context.MODE_PRIVATE)
            try { action(prefs) } catch (_: Exception) { null }
        }
    }

    private fun safeEdit(action: (SharedPreferences.Editor) -> Unit) {
        try {
            val editor = prefs.edit()
            action(editor)
            editor.apply()
        } catch (_: Exception) {
            prefs = appContext.getSharedPreferences("skyline_session_fallback", Context.MODE_PRIVATE)
            val editor = prefs.edit()
            action(editor)
            editor.apply()
        }
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USERNAME = "saved_username"
        private const val KEY_CACHED_DATA = "cached_subscription_json"
        private const val KEY_LAST_UPDATE = "last_update_millis"
    }

    fun saveSession(token: String, username: String) {
        safeEdit { editor ->
            editor.putString(KEY_TOKEN, token)
            editor.putString(KEY_USERNAME, username)
        }
    }

    fun getToken(): String? {
        return safeGet { it.getString(KEY_TOKEN, null) }
    }

    fun getUsername(): String? {
        return safeGet { it.getString(KEY_USERNAME, null) }
    }

    fun hasValidSession(): Boolean {
        val token = getToken()
        return !token.isNullOrBlank()
    }

    fun saveCache(subscription: UnifiedSubscription) {
        val json = ApiClient.gson.toJson(subscription)
        safeEdit { editor ->
            editor.putString(KEY_CACHED_DATA, json)
            editor.putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
        }
    }

    fun getCachedSubscription(): UnifiedSubscription? {
        val json = safeGet { it.getString(KEY_CACHED_DATA, null) } ?: return null
        return try {
            val sub = ApiClient.gson.fromJson(json, UnifiedSubscription::class.java)
            val lastUpdate = try { prefs.getLong(KEY_LAST_UPDATE, 0L) } catch (_: Exception) { 0L }
            sub?.copy(lastUpdatedMillis = lastUpdate, isFromCache = true)
        } catch (_: Exception) {
            null
        }
    }

    fun clearSession() {
        safeEdit { editor ->
            editor.remove(KEY_TOKEN)
            editor.remove(KEY_CACHED_DATA)
            editor.remove(KEY_LAST_UPDATE)
        }
    }
}
