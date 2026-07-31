package com.qrissender

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val PREF_MODE = "server_mode"
        const val PREF_LOCAL_URL = "local_url"
        const val PREF_DOMAIN_URL = "domain_url"
        const val PREF_API_KEY = "api_key"

        const val DEFAULT_LOCAL_URL = "http://192.168.1.17:8080"
        const val DEFAULT_DOMAIN_URL = "https://qris.chafis.my.id"
        const val DEFAULT_API_KEY = "rahasia12345"
    }

    private lateinit var modeSwitch: Switch
    private lateinit var urlInput: EditText
    private lateinit var apiKeyInput: EditText
    private lateinit var urlLabel: TextView
    private lateinit var modeLabel: TextView

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

        // Title
        val title = TextView(this).apply {
            text = "Settings"
            textSize = 20f
            setTextColor(Color.parseColor("#1A1A2E"))
            gravity = Gravity.CENTER
        }
        root.addView(title)

        // Mode Switch Card
        val modeCard = android.widget.LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(32, 24, 32, 24)
        }
        modeLabel = TextView(this).apply {
            text = "Mode: Local"
            textSize = 15f
            setTextColor(Color.parseColor("#1A1A2E"))
        }
        modeSwitch = Switch(this).apply {
            setOnCheckedChangeListener { _, isChecked ->
                updateMode(isChecked)
                saveSettings()
            }
        }
        modeCard.addView(modeLabel, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        modeCard.addView(modeSwitch)
        root.addView(modeCard, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 32, 0, 0)
        })

        // URL Label
        urlLabel = TextView(this).apply {
            text = "Server URL (Local)"
            textSize = 13f
            setTextColor(Color.parseColor("#1A1A2E"))
        }
        root.addView(urlLabel, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 24, 0, 8)
        })

        // URL Input
        urlInput = EditText(this).apply {
            hint = DEFAULT_LOCAL_URL
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

    private fun updateMode(isDomain: Boolean) {
        modeLabel.text = if (isDomain) "Mode: Domain" else "Mode: Local"
        urlLabel.text = if (isDomain) "Server URL (Domain)" else "Server URL (Local)"
        urlInput.hint = if (isDomain) DEFAULT_DOMAIN_URL else DEFAULT_LOCAL_URL
    }

    private fun loadSettings() {
        val prefs = getSharedPreferences("qris_sender_settings", Context.MODE_PRIVATE)
        val isDomain = prefs.getBoolean(PREF_MODE, false)
        val localUrl = prefs.getString(PREF_LOCAL_URL, DEFAULT_LOCAL_URL) ?: DEFAULT_LOCAL_URL
        val domainUrl = prefs.getString(PREF_DOMAIN_URL, DEFAULT_DOMAIN_URL) ?: DEFAULT_DOMAIN_URL

        modeSwitch.isChecked = isDomain
        updateMode(isDomain)
        urlInput.setText(if (isDomain) domainUrl else localUrl)
        apiKeyInput.setText(prefs.getString(PREF_API_KEY, DEFAULT_API_KEY))
    }

    private fun saveSettings() {
        val prefs = getSharedPreferences("qris_sender_settings", Context.MODE_PRIVATE)
        val isDomain = modeSwitch.isChecked
        val url = urlInput.text.toString().trim()
        val apiKey = apiKeyInput.text.toString().trim()

        prefs.edit().apply {
            putBoolean(PREF_MODE, isDomain)
            if (isDomain) {
                putString(PREF_DOMAIN_URL, url.ifBlank { DEFAULT_DOMAIN_URL })
            } else {
                putString(PREF_LOCAL_URL, url.ifBlank { DEFAULT_LOCAL_URL })
            }
            putString(PREF_API_KEY, apiKey.ifBlank { DEFAULT_API_KEY })
            apply()
        }
        Toast.makeText(this, "Pengaturan disimpan", Toast.LENGTH_SHORT).show()
        finish()
    }

    // Helper untuk dipakai di service/activity lain
    fun getActiveServerUrl(context: Context): String {
        val prefs = context.getSharedPreferences("qris_sender_settings", Context.MODE_PRIVATE)
        val isDomain = prefs.getBoolean(PREF_MODE, false)
        return if (isDomain) {
            prefs.getString(PREF_DOMAIN_URL, DEFAULT_DOMAIN_URL) ?: DEFAULT_DOMAIN_URL
        } else {
            prefs.getString(PREF_LOCAL_URL, DEFAULT_LOCAL_URL) ?: DEFAULT_LOCAL_URL
        }
    }
}
