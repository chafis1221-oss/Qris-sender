package com.qrissender

import android.app.Notification
import android.content.Context
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.*

class NotificationListenerService : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != "id.dana") return

        val title = sbn.notification.extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = sbn.notification.extras.getString(Notification.EXTRA_TEXT) ?: ""
        val fullText = "$title $text".trim()

        if (fullText.isBlank()) return

        scope.launch {
            val prefs = getSharedPreferences("qris_sender_settings", Context.MODE_PRIVATE)
            val isDomain = prefs.getBoolean(SettingsActivity.PREF_MODE, false)
            val url = if (isDomain) {
                prefs.getString(SettingsActivity.PREF_DOMAIN_URL, SettingsActivity.DEFAULT_DOMAIN_URL) ?: SettingsActivity.DEFAULT_DOMAIN_URL
            } else {
                prefs.getString(SettingsActivity.PREF_LOCAL_URL, SettingsActivity.DEFAULT_LOCAL_URL) ?: SettingsActivity.DEFAULT_LOCAL_URL
            }
            val apiKey = prefs.getString(SettingsActivity.PREF_API_KEY, SettingsActivity.DEFAULT_API_KEY) ?: SettingsActivity.DEFAULT_API_KEY

            if (url.isBlank() || apiKey.isBlank()) return@launch

            val success = ApiService.sendNotification(url, apiKey, fullText)
            HistoryManager.addItem(this@NotificationListenerService, fullText, success)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {}

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
