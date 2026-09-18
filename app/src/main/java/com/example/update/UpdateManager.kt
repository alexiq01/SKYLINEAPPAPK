package com.example.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Data model containing details about available application updates.
 */
data class AppUpdateInfo(
    val latestVersionCode: Long,
    val latestVersionName: String,
    val updateUrl: String,
    val isForceUpdate: Boolean,
    val currentVersionCode: Long,
    val currentVersionName: String,
    val title: String = "تحديث جديد متوفر",
    val message: String = "يتوفر إصدار جديد من التطبيق.",
    val releaseNotes: String = ""
)

/**
 * UpdateManager
 * 
 * Manages in-app updates based exclusively on Firebase Remote Config:
 * - Reads latest_version_code, latest_version_name, update_url, force_update
 * - Compares latest_version_code with BuildConfig.VERSION_CODE
 * - Triggers optional or forced update dialog with update_url
 */
object UpdateManager {
    private const val TAG = "UpdateManager"

    // Remote Config Keys specified
    const val KEY_LATEST_VERSION_CODE = "latest_version_code"
    const val KEY_LATEST_VERSION_NAME = "latest_version_name"
    const val KEY_UPDATE_URL = "update_url"
    const val KEY_FORCE_UPDATE = "force_update"

    // Optional keys if available
    const val KEY_UPDATE_TITLE = "update_title"
    const val KEY_UPDATE_MESSAGE = "update_message"
    const val KEY_RELEASE_NOTES = "release_notes"

    private val _updateState = MutableStateFlow<AppUpdateInfo?>(null)
    val updateState: StateFlow<AppUpdateInfo?> = _updateState.asStateFlow()

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()

    private var isInitialized = false

    /**
     * Initializes Firebase Remote Config with default fallback parameters and settings.
     */
    fun initialize(context: Context) {
        if (isInitialized) return
        isInitialized = true

        try {
            val remoteConfig = FirebaseRemoteConfig.getInstance()
            val fetchInterval = if (BuildConfig.DEBUG) 0L else 3600L // 1 hour cache in production, instant in debug

            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(fetchInterval)
                .setFetchTimeoutInSeconds(15)
                .build()

            remoteConfig.setConfigSettingsAsync(configSettings)

            val defaults = mapOf<String, Any>(
                KEY_LATEST_VERSION_CODE to BuildConfig.VERSION_CODE.toString(),
                KEY_LATEST_VERSION_NAME to BuildConfig.VERSION_NAME,
                KEY_UPDATE_URL to "",
                KEY_FORCE_UPDATE to "false",
                KEY_UPDATE_TITLE to "تحديث جديد متوفر",
                KEY_UPDATE_MESSAGE to "يتوفر إصدار جديد من تطبيق سكايلان.",
                KEY_RELEASE_NOTES to "تحسينات وإصلاحات جديدة."
            )
            remoteConfig.setDefaultsAsync(defaults)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Remote Config: ${e.message}")
        }
    }

    /**
     * Checks for updates by fetching and activating Remote Config.
     * Compares latest_version_code against BuildConfig.VERSION_CODE.
     */
    fun checkForUpdates(
        context: Context,
        forceImmediateFetch: Boolean = false,
        onUpdateFound: ((AppUpdateInfo) -> Unit)? = null,
        onNoUpdate: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        initialize(context)

        CoroutineScope(Dispatchers.IO).launch {
            _isChecking.value = true
            try {
                val remoteConfig = FirebaseRemoteConfig.getInstance()

                if (forceImmediateFetch) {
                    val settings = FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(0)
                        .setFetchTimeoutInSeconds(10)
                        .build()
                    remoteConfig.setConfigSettingsAsync(settings).await()
                }

                // 1. Fetch & activate latest values from Firebase Remote Config
                remoteConfig.fetchAndActivate().await()

                // 2. Read values (handles both String and Number types in Remote Config)
                val rawVersionCodeStr = remoteConfig.getString(KEY_LATEST_VERSION_CODE).trim()
                val latestVersionCode = rawVersionCodeStr.toLongOrNull() ?: remoteConfig.getLong(KEY_LATEST_VERSION_CODE)

                val latestVersionName = remoteConfig.getString(KEY_LATEST_VERSION_NAME).ifBlank {
                    remoteConfig.getString("latest_version").ifBlank { "1.0" }
                }

                val updateUrl = remoteConfig.getString(KEY_UPDATE_URL).trim()

                val rawForceUpdate = remoteConfig.getString(KEY_FORCE_UPDATE).trim()
                val isForceUpdate = rawForceUpdate.equals("true", ignoreCase = true) || remoteConfig.getBoolean(KEY_FORCE_UPDATE)

                val title = remoteConfig.getString(KEY_UPDATE_TITLE).ifBlank { "تحديث جديد متوفر" }
                val message = remoteConfig.getString(KEY_UPDATE_MESSAGE).ifBlank { "يتوفر إصدار جديد من التطبيق." }
                val releaseNotes = remoteConfig.getString(KEY_RELEASE_NOTES).ifBlank { "تحسينات وإصلاحات جديدة." }

                val currentVersionCode = BuildConfig.VERSION_CODE.toLong()
                val currentVersionName = BuildConfig.VERSION_NAME

                Log.d(TAG, "Update Check: current=$currentVersionCode ($currentVersionName), latest=$latestVersionCode ($latestVersionName), url=$updateUrl, force=$isForceUpdate")

                // 3. Compare with BuildConfig.VERSION_CODE
                if (latestVersionCode > currentVersionCode && updateUrl.isNotBlank()) {
                    val updateInfo = AppUpdateInfo(
                        latestVersionCode = latestVersionCode,
                        latestVersionName = latestVersionName,
                        updateUrl = updateUrl,
                        isForceUpdate = isForceUpdate,
                        currentVersionCode = currentVersionCode,
                        currentVersionName = currentVersionName,
                        title = title,
                        message = message,
                        releaseNotes = releaseNotes
                    )

                    _updateState.value = updateInfo
                    launch(Dispatchers.Main) {
                        onUpdateFound?.invoke(updateInfo)
                    }
                } else {
                    launch(Dispatchers.Main) {
                        onNoUpdate?.invoke()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to check for updates: ${e.message}", e)
                launch(Dispatchers.Main) {
                    onError?.invoke(e.localizedMessage ?: "فشل التحقق من التحديثات")
                }
            } finally {
                _isChecking.value = false
            }
        }
    }

    /**
     * Dismisses the optional update prompt.
     */
    fun dismissUpdate() {
        val current = _updateState.value
        if (current != null && !current.isForceUpdate) {
            _updateState.value = null
        }
    }

    /**
     * Launches the update URL in the browser / downloader.
     */
    fun openUpdateUrl(context: Context, url: String) {
        try {
            if (url.isBlank()) return
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to open update URL: ${e.message}")
        }
    }
}
