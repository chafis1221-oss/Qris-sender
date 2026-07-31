package com.qrissender

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object ApiService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(3, TimeUnit.SECONDS)
        .build()

    suspend fun sendWithFallback(
        localUrl: String,
        domainUrl: String,
        apiKey: String,
        text: String
    ): Boolean {
        // Coba local dulu
        val localOk = sendNotification(localUrl, apiKey, text)
        if (localOk) {
            Log.d("QRIS Sender", "Sent via LOCAL")
            return true
        }

        // Local gagal, coba domain
        Log.d("QRIS Sender", "Local failed, trying DOMAIN")
        return sendNotification(domainUrl, apiKey, text)
    }

    private suspend fun sendNotification(serverUrl: String, apiKey: String, text: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply { put("text", text) }
                val body = json.toString().toRequestBody("application/json".toMediaType())
                val request = Request.Builder()
                    .url("$serverUrl/webhook")
                    .header("x-api-key", apiKey)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("QRIS Sender", "Error: $serverUrl", e)
                false
            }
        }
    }
}
