package com.qrissender

import android.app.Notification
import android.content.Context
import android.content.SharedPreferences
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
            val serverUrl = prefs.getString("server_url", "") ?: ""
            val apiKey = prefs.getString("api_key", "") ?: ""

            if (serverUrl.isBlank() || apiKey.isBlank()) return@launch

            val success = ApiService.sendNotification(serverUrl, apiKey, fullText)
            HistoryManager.addItem(this@NotificationListenerService, fullText, success)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        // Not needed
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
