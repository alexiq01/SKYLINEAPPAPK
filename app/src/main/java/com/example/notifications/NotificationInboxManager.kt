package com.example.notifications

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Data model for an individual notification item received by the user.
 */
data class AppNotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val type: String, // e.g. "expired", "expiring_soon", "system", "general"
    val isRead: Boolean = false
) {
    val formattedTime: String
        get() {
            return try {
                val sdf = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale.getDefault())
                sdf.format(Date(timestamp))
            } catch (_: Exception) {
                ""
            }
        }
}

/**
 * Storage and management for received in-app notifications.
 * Stores notifications as they arrive so the user sees a real notifications inbox.
 */
object NotificationInboxManager {
    private const val PREFS_NAME = "skyline_notifications_inbox"
    private const val KEY_ITEMS = "stored_notification_items"

    private val _notificationsFlow = MutableStateFlow<List<AppNotificationItem>>(emptyList())
    val notificationsFlow: StateFlow<List<AppNotificationItem>> = _notificationsFlow.asStateFlow()

    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            _notificationsFlow.value = loadFromStorage(context)
            isInitialized = true
        }
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Synchronized
    fun addNotification(
        context: Context,
        title: String,
        message: String,
        type: String = "general"
    ) {
        init(context)
        val newItem = AppNotificationItem(
            id = System.currentTimeMillis().toString() + "_" + (100..999).random(),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            type = type,
            isRead = false
        )
        val currentList = _notificationsFlow.value.toMutableList()
        currentList.add(0, newItem) // Newest first

        // Keep maximum 50 notifications in inbox
        if (currentList.size > 50) {
            currentList.removeAt(currentList.lastIndex)
        }

        _notificationsFlow.value = currentList
        saveToStorage(context, currentList)
    }

    @Synchronized
    fun markAllAsRead(context: Context) {
        init(context)
        val updated = _notificationsFlow.value.map { it.copy(isRead = true) }
        _notificationsFlow.value = updated
        saveToStorage(context, updated)
    }

    @Synchronized
    fun clearAll(context: Context) {
        _notificationsFlow.value = emptyList()
        saveToStorage(context, emptyList())
    }

    private fun saveToStorage(context: Context, list: List<AppNotificationItem>) {
        try {
            val array = JSONArray()
            for (item in list) {
                val obj = JSONObject().apply {
                    put("id", item.id)
                    put("title", item.title)
                    put("message", item.message)
                    put("timestamp", item.timestamp)
                    put("type", item.type)
                    put("isRead", item.isRead)
                }
                array.put(obj)
            }
            getPrefs(context).edit().putString(KEY_ITEMS, array.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun loadFromStorage(context: Context): List<AppNotificationItem> {
        val list = mutableListOf<AppNotificationItem>()
        try {
            val raw = getPrefs(context).getString(KEY_ITEMS, null) ?: return list
            val array = JSONArray(raw)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    AppNotificationItem(
                        id = obj.optString("id", i.toString()),
                        title = obj.optString("title", ""),
                        message = obj.optString("message", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        type = obj.optString("type", "general"),
                        isRead = obj.optBoolean("isRead", false)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }
}
