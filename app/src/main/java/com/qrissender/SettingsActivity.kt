package com.qrissender

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val PREF_DOMAIN_URL = "domain_url"
        const val PREF_API_KEY = "api_key"

        const val LOCAL_URL = "http://192.168.1.17:8080"
        const val DEFAULT_DOMAIN_URL = "https://qris.chafis.my.id"
        const val DEFAULT_API_KEY = "rahasia12345"
    }

    private lateinit var urlInput: EditText
    private lateinit var apiKeyInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createLayout())
        loadSettings()
    }

    private fun createLayout(): LinearLayout {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            setBackgroundColor(Color.parseColor("#F8F9FA"))
        }

        val title = TextView(this).apply {
            text = "Settings"
            textSize = 20f
            setTextColor(Color.parseColor("#1A1A2E"))
            gravity = Gravity.CENTER
        }
        root.addView(title)

        // Info box
        val infoCard = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setBackgroundColor(Color.parseColor("#1A1A2E").withAlpha(10))
            setPadding(20, 16, 20, 16)
        }
        val infoText = TextView(this).apply {
            text = "Aplikasi otomatis coba kirim ke jaringan lokal. Kalau gagal, pakai domain di bawah."
            textSize = 13f
            setTextColor(Color.parseColor("#757575"))
        }
        infoCard.addView(infoText)
        root.addView(infoCard, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 32, 0, 0)
        })

        // Domain URL Label
        val urlLabel = TextView(this).apply {
            text = "Domain URL"
            textSize = 13f
            setTextColor(Color.parseColor("#1A1A2E"))
        }
        root.addView(urlLabel, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 24, 0, 8)
        })

        // Domain URL Input
        urlInput = EditText(this).apply {
            hint = DEFAULT_DOMAIN_URL
            setTextColor(Color.parseColor("#1A1A2E"))
            setHintTextColor(Color.parseColor("#BDBDBD"))
            setBackgroundColor(Color.WHITE)
            setPadding(32, 24, 32, 24)
        }
        root.addView(urlInput, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        // API Key Label
        val apiLabel = TextView(this).apply {
            text = "API Key"
            textSize = 13f
            setTextColor(Color.parseColor("#1A1A2E"))
        }
        root.addView(apiLabel, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 24, 0, 8)
        })

        // API Key Input
        apiKeyInput = EditText(this).apply {
            hint = DEFAULT_API_KEY
            setTextColor(Color.parseColor("#1A1A2E"))
            setHintTextColor(Color.parseColor("#BDBDBD"))
            setBackgroundColor(Color.WHITE)
            setPadding(32, 24, 32, 24)
        }
        root.addView(apiKeyInput, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))

        // Save Button
        val saveButton = Button(this).apply {
            text = "Simpan"
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#1A1A2E"))
            setOnClickListener { saveSettings() }
        }
        root.addView(saveButton, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 32, 0, 0)
        })

        return root
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("qris_sender_settings", Context.MODE_PRIVATE)
        urlInput.setText(prefs.getString(PREF_DOMAIN_URL, DEFAULT_DOMAIN_URL))
        apiKeyInput.setText(prefs.getString(PREF_API_KEY, DEFAULT_API_KEY))
    }

    private fun saveSettings() {
        val prefs = getSharedPreferences("qris_sender_settings", Context.MODE_PRIVATE)
        val url = urlInput.text.toString().trim().ifBlank { DEFAULT_DOMAIN_URL }
        val apiKey = apiKeyInput.text.toString().trim().ifBlank { DEFAULT_API_KEY }

        prefs.edit().apply {
            putString(PREF_DOMAIN_URL, url)
            putString(PREF_API_KEY, apiKey)
            apply()
        }
        Toast.makeText(this, "Pengaturan disimpan", Toast.LENGTH_SHORT).show()
        finish()
    }

    companion object {
        fun getActiveUrl(context: Context): String {
            val prefs = context.getSharedPreferences("qris_sender_settings", Context.MODE_PRIVATE)
            return prefs.getString(PREF_DOMAIN_URL, DEFAULT_DOMAIN_URL) ?: DEFAULT_DOMAIN_URL
        }
    }
}
