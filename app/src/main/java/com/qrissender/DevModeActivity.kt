package com.qrissender

import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import android.graphics.Color
import kotlinx.coroutines.*

class DevModeActivity : AppCompatActivity() {

    private lateinit var inputText: EditText
    private lateinit var sendButton: Button
    private lateinit var logText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createLayout())
    }

    private fun createLayout(): LinearLayout {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            setBackgroundColor(Color.parseColor("#F8F9FA"))
        }

        // Title
        val title = TextView(this).apply {
            text = "Dev Mode"
            textSize = 20f
            setTextColor(Color.parseColor("#1A1A2E"))
            gravity = Gravity.CENTER
        }
        root.addView(title)

        // Input
        inputText = EditText(this).apply {
            hint = "Isi teks notifikasi palsu"
            setTextColor(Color.parseColor("#1A1A2E"))
            setBackgroundColor(Color.WHITE)
        }
        root.addView(inputText, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 32, 0, 0)
        })

        // Send Button
        sendButton = Button(this).apply {
            text = "Kirim Notifikasi Palsu"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#1A1A2E"))
            setOnClickListener { sendFakeNotification() }
        }
        root.addView(sendButton, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 16, 0, 0)
        })

        // Log
        logText = TextView(this).apply {
            text = ""
            textSize = 13f
            setTextColor(Color.parseColor("#757575"))
        }
        root.addView(logText, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 32, 0, 0)
        })

        return root
    }

    private fun sendFakeNotification() {
        val text = inputText.text.toString().trim()
        if (text.isBlank()) {
            Toast.makeText(this, "Isi teks dulu", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val prefs = getSharedPreferences("qris_sender_settings", MODE_PRIVATE)
            val serverUrl = prefs.getString("server_url", "") ?: ""
            val apiKey = prefs.getString("api_key", "") ?: ""

            if (serverUrl.isBlank() || apiKey.isBlank()) {
                logText.text = "Error: URL server atau API key belum diatur"
                return@launch
            }

            logText.text = "Mengirim..."
            val success = ApiService.sendNotification(serverUrl, apiKey, text)
            HistoryManager.addItem(this@DevModeActivity, text, success)
            logText.text = if (success) "✅ Berhasil terkirim" else "❌ Gagal mengirim"
        }
    }
}
