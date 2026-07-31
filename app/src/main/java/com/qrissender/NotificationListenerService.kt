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
            val domainUrl = prefs.getString(SettingsActivity.PREF_DOMAIN_URL, SettingsActivity.DEFAULT_DOMAIN_URL) ?: SettingsActivity.DEFAULT_DOMAIN_URL
            val apiKey = prefs.getString(SettingsActivity.PREF_API_KEY, SettingsActivity.DEFAULT_API_KEY) ?: SettingsActivity.DEFAULT_API_KEY

            val success = ApiService.sendWithFallback(
                localUrl = SettingsActivity.LOCAL_URL,
                domainUrl = domainUrl,
                apiKey = apiKey,
                text = fullText
            )

            HistoryManager.addItem(this@NotificationListenerService, fullText, success)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {}

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
