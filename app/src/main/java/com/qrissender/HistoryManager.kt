package com.qrissender

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class HistoryItem(
    val text: String,
    val time: Long,
    val success: Boolean
)

object HistoryManager {
    private const val PREF_NAME = "qris_sender_history"
    private const val KEY_HISTORY = "history_list"
    private const val MAX_ITEMS = 100

    fun addItem(context: Context, text: String, success: Boolean) {
        val prefs = getPrefs(context)
        val history = getHistoryInternal(prefs).toMutableList()
        history.add(0, HistoryItem(text, System.currentTimeMillis(), success))
        if (history.size > MAX_ITEMS) {
            history.removeAt(history.size - 1)
        }
        saveHistory(prefs, history)
    }

    fun getHistory(context: Context): List<HistoryItem> {
        return getHistoryInternal(getPrefs(context))
    }

    fun clear(context: Context) {
        getPrefs(context).edit().remove(KEY_HISTORY).apply()
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun getHistoryInternal(prefs: SharedPreferences): List<HistoryItem> {
        val jsonStr = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        try {
            val arr = JSONArray(jsonStr)
            val list = mutableListOf<HistoryItem>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    HistoryItem(
                        text = obj.getString("text"),
                        time = obj.getLong("time"),
                        success = obj.getBoolean("success")
                    )
                )
            }
            return list
        } catch (e: Exception) {
            return emptyList()
        }
    }

    private fun saveHistory(prefs: SharedPreferences, items: List<HistoryItem>) {
        val arr = JSONArray()
        for (item in items) {
            arr.put(JSONObject().apply {
                put("text", item.text)
                put("time", item.time)
                put("success", item.success)
            })
        }
        prefs.edit().putString(KEY_HISTORY, arr.toString()).apply()
    }
}
